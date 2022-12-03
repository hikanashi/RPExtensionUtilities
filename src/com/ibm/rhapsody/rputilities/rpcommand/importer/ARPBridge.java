package com.ibm.rhapsody.rputilities.rpcommand.importer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenType;
import com.ibm.rhapsody.rputilities.doxygen.DoxygenTypeCompound;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPTag;
import com.telelogic.rhapsody.core.IRPType;

public abstract class ARPBridge extends ARPObject {
    protected final String TAG_VERSION_PACKAGE = "Version";
    protected final String TAG_VERSION_APPLICABLE = "FirstApplicableVersion";
    protected final String TAG_VERSION_UNAVAILABLE = "UnavailableStartVersion";
    protected final String TAG_EXCLUDED_ELEMENT = "Excluded";
    protected final String ELEMENT_NAME_CHANGE_PREFIX = "Changed";
    protected final String ELEMENT_NAME_DELETE_PREFIX = "Deleted";

    DoxygenType doxygen_ = null;
    IRPPackage rootPackage_ = null;

    protected ARPBridge(Class<?> clazz, DoxygenType doxygen, IRPPackage rootPackage) {
        super(clazz);
        doxygen_ = doxygen;
        rootPackage_ = rootPackage;

    }

    abstract protected List<IRPModelElement> getElementsByType(IRPPackage rpPackage);
    abstract protected IRPModelElement findElementByType(IRPPackage rpPackage);
    abstract protected IRPModelElement createElementByType(IRPPackage modulPackage);
    abstract protected boolean isUpdate(IRPModelElement element);
    abstract protected void applyByType(IRPModelElement element, String currentVersion);


    public IRPModelElement importElement(String currentVersion) {

        IRPModelElement current_element = findElement(rootPackage_);
        IRPModelElement unavailable_element = null; 
        String baseVersion = getBaseVersion(current_element);
        boolean update = false;
        int compareResult = 0;

        if( current_element != null ) {   
            compareResult = compareVersion(baseVersion, currentVersion);
            update = isUpdate(current_element);
        }

        if( update == true && compareResult < 0) {
            unavailable_element = current_element;
            current_element = null;
        }

        if(compareResult > 0) {
            warn(String.format("Version %s is older than %s. so can't import.",
                     currentVersion, baseVersion));
            return null;
        }

        IRPPackage versionPackage = createVersionPackage(currentVersion);
        IRPPackage modulePackage = createModulePackage(versionPackage);

        if( current_element != null ) {
            apply(current_element, modulePackage, currentVersion);
            return current_element;
        }
        
        current_element = createElementByType(modulePackage);
        if(current_element == null) {
            return null;
        }

        apply(current_element, modulePackage, currentVersion);

        changedElement(unavailable_element, current_element, currentVersion);

        return current_element;
    }



    public IRPModelElement findElement(IRPPackage rpPackage) {
        if(rpPackage == null) { 
            return null;
        }

        IRPModelElement element = findElementByType(rpPackage);

        if(element != null) {
            return element;
        }
        
        List<IRPPackage> packages = toList(rpPackage.getPackages());

        for(IRPPackage subPackage :  packages) {
            element = findElement(subPackage);
            if(element != null) {
                return element;
            }
        }

        return null;
    }

    public void replaceOldElement(String currentVersion) {

        Set<IRPModelElement> list = getElements(rootPackage_, currentVersion);

        debug(String.format("replaceOldElement:%s version:%s target:%d",
                getClass().getSimpleName(),
                currentVersion,
                list.size()));

        for(IRPModelElement element : list) {
            if( isDeleteTarget(element) != true) {
                continue;
            }

            deletedElement(element, null, currentVersion);
        }

        return;
    }

    public Set<IRPModelElement> getElements(IRPPackage rpPackage, String currentVersion) {
        Set<IRPModelElement> elements = new HashSet<IRPModelElement>();
        if(rpPackage == null) { 
            return null;
        }

        if( rpPackage.getGUID().equals(rootPackage_.getGUID()) != true ) {
            String version = getBaseVersion(rpPackage);
            int compareResult = compareVersion(version, currentVersion);
            if( compareResult >= 0) {
                debug(String.format("getElements package:%s(version:%s) isn't old current(%s):%d",
                    rpPackage.getName(),
                    version,
                    currentVersion,
                    compareResult));
                return null;
            } else {
                debug(String.format("getElements package:%s(version:%s) is target old current(%s):%d",
                    rpPackage.getName(),
                    version,
                    currentVersion,
                    compareResult));  
            }

            List<IRPModelElement> list = getElementsByType(rpPackage);
            if(list != null) {
                elements.addAll(list);
            }
        }

        List<IRPPackage> packages = toList(rpPackage.getPackages());
        for(IRPPackage subPackage :  packages) {
            if( isDeleteTarget(subPackage) != true ) {
                continue;
            }

            Set<IRPModelElement> sublist =  getElements(subPackage, currentVersion);
            if( sublist != null) {
                elements.addAll(sublist);
            }
        }

        return elements;
    }

    protected boolean isDeleteTarget(IRPModelElement element) {
        if( element == null ) {
            return false;
        }

        String[] notTargetTag = {TAG_VERSION_UNAVAILABLE,TAG_EXCLUDED_ELEMENT};

        for(int index=0; index < notTargetTag.length; index++) {
            IRPTag rptag = element.getTag(notTargetTag[index]);
            if( rptag != null ) {
                debug(String.format("element:%s has tag:%s. so excluded delete.", 
                            element.getName(),
                            rptag.getName()));
                return false;
            }
        }

        return true;
    }

    protected IRPModelElement createElement(IRPPackage versionPackage) {
        IRPPackage targetPackage = createModulePackage(versionPackage);
        IRPModelElement element = createElementByType(targetPackage); 
        return element;
    }

    protected void apply(IRPModelElement element, IRPPackage modulePackage, String currentVersion) {

        updateOwner(element,modulePackage);
        setApplicableVersion(element, currentVersion);
        applyByType(element, currentVersion);
    }

    protected void updateOwner(IRPModelElement currentElement, IRPModelElement ownerElement) {
        IRPPackage ownerPackage = getPackage(currentElement);
        String ownerID = "";
        String ownerName = "";
        if(getPackage(currentElement) != null) {
            ownerID = ownerPackage.getGUID();
            ownerName = ownerPackage.getName();
        }
        if( ownerID.equals(ownerElement.getGUID()) != true ) {
            trace(String.format("element:%s's owner %s(%s)->%s(%s)",
                    currentElement.getName(),
                    ownerName,
                    ownerID,
                    ownerElement.getName(),
                    ownerElement.getGUID()));
            currentElement.setOwner(ownerElement);
        }
    }

    protected IRPType CreateType(DoxygenType param, String currentVersion) {
        IRPType type = null;
        RPParamTypeBridge parambridge = new RPParamTypeBridge(param, rootPackage_);
        type = getObject(parambridge.importElement(currentVersion));
        return type;
    }

    protected boolean setApplicableVersion(IRPModelElement rpelement, String version) {
        return setTagOnlyOnce(rpelement,TAG_VERSION_APPLICABLE,version);
    }

    protected boolean setUnabavailableVersion(IRPModelElement rpelement, String version) {
        return setTagOnlyOnce(rpelement,TAG_VERSION_UNAVAILABLE,version);
    }

    protected void deletedElement(IRPModelElement unavailable, IRPModelElement current, String currentVersion) {
        unavailableElement(unavailable, current, ELEMENT_NAME_DELETE_PREFIX, currentVersion);
    }

    protected void changedElement(IRPModelElement unavailable, IRPModelElement current, String currentVersion) {
        unavailableElement(unavailable, current, ELEMENT_NAME_CHANGE_PREFIX, currentVersion);
    }

    protected void unavailableElement(IRPModelElement unavailable, IRPModelElement current, String prefix, String currentVersion) {
        if( unavailable == null ) {
            return;
        }

        setUnabavailableVersion(unavailable,currentVersion);

        String change_name = String.format("%s_%s_%s", 
                                prefix,
                                convertAvailableName(currentVersion),
                                unavailable.getName());
        debug(String.format("change unavailable %s->%s",
                    unavailable.getName(), change_name ));
        unavailable.setName(change_name);

        if( current == null ) {
            return;
        }

        // TODO addLinkToElement

    }

    protected boolean setTagOnlyOnce(IRPModelElement rpelement, String tagname, String tagvalue) {
        if(rpelement == null || tagname == null || tagvalue == null) {
            return false;
        }

        IRPTag versiontag = rpelement.getTag(tagname);
        if(versiontag != null ) {
            return false;
        }

        versiontag = getObject(rpelement.addNewAggr("Tag",tagname));
        versiontag.setMultiplicity("1");
        rpelement.setTagValue(versiontag, tagvalue);

        return true;
    }

    protected int compareVersion(String srcVersion, String destVersion) {
        int compare = srcVersion.compareTo(destVersion);
        trace("srcVersion:"+ srcVersion + " dstVersion:" + destVersion + " result:" + compare);
        return compare;
    }

    public String getBaseVersion(IRPModelElement rpelement) {
        String baseVersion = "";
        if(rpelement == null) {
            return baseVersion;
        }

        IRPPackage versionPackage = GetBaseVersionPackage(rpelement);
        if(versionPackage == null) {
            return baseVersion;
        }

        IRPTag versiontag = versionPackage.getTag(TAG_VERSION_PACKAGE);
        if(versiontag == null ) {
            return baseVersion;
        }

        return versiontag.getValue();
    }


    public IRPPackage GetBaseVersionPackage(IRPModelElement rpelement) {
        if( rpelement == null ) {
            return null;
        }

        for(IRPModelElement checkElement = rpelement;
            checkElement != null;
            checkElement = checkElement.getOwner())  {

            if(rootPackage_.getGUID().equals(rpelement.getGUID()) == true) {
                return null;
            }

            if(!(checkElement instanceof IRPPackage)) {
                continue;
            }

            IRPTag versiontag = checkElement.getTag(TAG_VERSION_PACKAGE);
            if(versiontag == null ) {
                continue;
            }

            return getObject(checkElement) ;
        }
        
        return null;
    }

    public IRPPackage createVersionPackage(String version) {
        if( version == null) {
            return null;
        }

        String modelname = convertAvailableName(version);

        IRPPackage versionPackage = null;
        IRPModelElement checkElement = rootPackage_.findNestedElementRecursive(modelname,"Package");
        if(checkElement == null) {
            debug("create version package:"+ modelname + "(" + version + ")");
            versionPackage = rootPackage_.addNestedPackage(modelname);
        }
        else {
            versionPackage = getObject(checkElement);
            trace(version + "is allready exist Package:"+ checkElement.getFullPathName());
        }

        if(versionPackage == null) {
            error("Create Package fail. prease check version name:"+ version);
            return null;
        }

        IRPTag versiontag = versionPackage.getTag(TAG_VERSION_PACKAGE);
        if(versiontag == null ) {
            versiontag = getObject(versionPackage.addNewAggr("Tag",TAG_VERSION_PACKAGE));
        }
        else {
            if( versiontag.getValue().equals(version) == true) {
                return versionPackage;
            }
        }

        versionPackage.setTagValue(versiontag, version);       
        return versionPackage;
    }

    public IRPPackage createModulePackage(IRPPackage versionPackage) {
        if(versionPackage == null ) {
            return null;
        }

        IRPPackage parentPackage = versionPackage;

        for(DoxygenType moduleType = doxygen_.getParent();
            moduleType != null;
            moduleType = moduleType.getParent())  {

            if(! (moduleType instanceof DoxygenTypeCompound)) {
                continue;
            }

            String packageName = convertAvailableName(moduleType.getName());

            IRPModelElement element = parentPackage.findAllByName(packageName, "Package");
            if( element == null ) {
                debug(parentPackage.getName() + " is not found, Create Package:"+ packageName);
                element = parentPackage.addNestedPackage(packageName);
            } else {
                trace(parentPackage.getName() + " is found "+ packageName); 
            }

            parentPackage = getObject(element);
        }
        
        return parentPackage;
    }


    protected String convertAvailableName( String name ) {
        return name.replaceAll("\\.|-", "_");
    }

}