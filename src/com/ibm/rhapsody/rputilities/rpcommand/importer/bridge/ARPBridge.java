package com.ibm.rhapsody.rputilities.rpcommand.importer.bridge;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenTypeFile;
import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenTypeGroup;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPTag;
import com.telelogic.rhapsody.core.IRPType;

public abstract class ARPBridge extends ARPObject {
    protected final String TAG_VERSION_PACKAGE = "Version";
    protected final String TAG_VERSION_APPLICABLE = "FirstApplicableVersion";
    protected final String TAG_VERSION_UNAVAILABLE = "UnavailableStartVersion";
    protected final String TAG_EXCLUDED_ELEMENT = "Excluded";
    protected final String ELEMENT_NAME_CHANGE_PREFIX = "Changed";
    protected final String ELEMENT_NAME_DELETE_PREFIX = "Deleted";
    protected final String STEREOTYPE_VALUETYPE = "ValueType";

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
            changedElement(unavailable_element, currentVersion);
            current_element = null;
        }

        if(compareResult > 0) {
            warn(String.format("Version %s is older than %s. so can't import.",
                     currentVersion, baseVersion));
            return null;
        }

        IRPPackage versionPackage = createVersionPackage(currentVersion);
        IRPPackage modulePackage = createModulePackage(versionPackage);
        if(modulePackage == null) {
            return null;
        }

        if( current_element != null ) {
            apply(current_element, modulePackage, currentVersion);
            return current_element;
        }
        
        current_element = createElement(modulePackage);
        if(current_element == null) {
            return null;
        }

        apply(current_element, modulePackage, currentVersion);

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

        info(String.format("replaceOldElement:%s version:%s target:%d",
                getClass().getSimpleName(),
                currentVersion,
                list.size()));

        for(IRPModelElement element : list) {
            if( isDeleteTarget(element) != true) {
                continue;
            }

            deletedElement(element, currentVersion);
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

    protected IRPModelElement createElement(IRPPackage targetPackage) {
        IRPModelElement element  = null;
        element = createElementByType(targetPackage); 

        return element;
    }

    protected void apply(IRPModelElement element, IRPPackage modulePackage, String currentVersion) {
        try {
            if(element instanceof IRPType) {
                IRPType rpType = getObject(element);
                if( rpType.getIsPredefined() == 0) {
                    setStereoType(element, STEREOTYPE_VALUETYPE);
                }
            }
    
            updateOwner(element,modulePackage);
            setApplicableVersion(element, currentVersion);
            applyByType(element, currentVersion);
        } catch (Exception e) {
            error("apply Error:", e);
        }
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
        RPBridgeParamType parambridge = new RPBridgeParamType(param, rootPackage_);
        type = getObject(parambridge.importElement(currentVersion));
        return type;
    }

    protected boolean setApplicableVersion(IRPModelElement rpelement, String version) {
        return setTagOnlyOnce(rpelement,TAG_VERSION_APPLICABLE,version);
    }

    protected boolean setUnabavailableVersion(IRPModelElement rpelement, String version) {
        return setTagOnlyOnce(rpelement,TAG_VERSION_UNAVAILABLE,version);
    }

    protected void deletedElement(IRPModelElement unavailable, String currentVersion) {
        unavailableElement(unavailable, ELEMENT_NAME_DELETE_PREFIX, currentVersion);
    }

    protected void changedElement(IRPModelElement unavailable, String currentVersion) {
        unavailableElement(unavailable, ELEMENT_NAME_CHANGE_PREFIX, currentVersion);
    }

    protected void unavailableElement(IRPModelElement unavailable, String prefix, String currentVersion) {
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

    protected void setStereoType(IRPModelElement rpelement, String stereotypeName) {
        if(rpelement == null || stereotypeName == null) {
            return;
        }

        return;

        // IRPStereotype stereo = findStereoType(rpelement, stereotypeName);
        // if( stereo != null ) {
        //     return;
        // }

        // stereo = findProjectStereoType(rpelement, stereotypeName);
        // if( stereo == null ) {
        //     warn(String.format("Stereotype:%s is not found. so Element:%s can't set.",
        //             stereotypeName, rpelement.getName()));
        //     return;
        // }

        
        // try {
        //     rpelement.addSpecificStereotype(stereo);
        // } catch (Exception e) {
        //     error(String.format("addSpecificStereotype Error Name:%s stereotype:%s(%s)",
        //         rpelement.getName(),
        //         stereotypeName,
        //         (stereo != null ? stereo.getName() : "null")), e);
        // }

    }

    protected IRPStereotype findStereoType(IRPModelElement rpelement, String stereotypeName) {
        if(rpelement == null || stereotypeName == null) {
            return null;
        }

        List<IRPStereotype> stereotypes = toList(rpelement.getStereotypes());

        for(IRPStereotype stereo : stereotypes) {
            if(stereo.getName().equals(stereotypeName) == true) {
                return stereo;
            }
        }
        return null;
    }

    protected IRPStereotype findProjectStereoType(IRPModelElement rpelement, String stereotypeName) {
        if(rpelement == null || stereotypeName == null) {
            return null;
        }

        IRPProject rpProject = rpelement.getProject();

        List<IRPStereotype> stereotypes = toList(rpProject.getAllStereotypes());

        for(IRPStereotype stereo : stereotypes) {
            if(stereo.getName().equals(stereotypeName) == true) {
                return stereo;
            }
        }
        return null;
    }

    protected IRPModelElement findNestedElementRecursive(IRPPackage rppackage, String name, String metaClass) {
        if(rppackage == null || name == null || metaClass == null) {
            return null;
        }

        // return rppackage.findNestedElementRecursive(name, metaClass);

        IRPModelElement element = null;
        List<IRPModelElement> childlen = null;
        childlen = toList(rppackage.getNestedElementsRecursive());        
        for(IRPModelElement child : childlen) {
            if(child.getMetaClass().equals(metaClass) != true) {
                continue;
            }
        
            if(child.getName().equals(name) != true) {
                continue;
            }
        
            element = child;
            break;
        }
        
        if(element != null){
            return element;
        }
        
        List<IRPPackage> childpackages = toList(rppackage.getPackages());       
        for(IRPPackage childpackage : childpackages) {
            element = findNestedElementRecursive(childpackage, name, metaClass);
            if( element != null) {
                return element;
            }
        }
        
        return null;
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

        String modelname = rootPackage_.getName() + "_" + convertAvailableName(version);
        trace("createVersionPackage:" + modelname);

        IRPPackage versionPackage = null;
        IRPModelElement checkElement = findNestedElementRecursive(rootPackage_,modelname,"Package");
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

            if(!(moduleType instanceof DoxygenTypeGroup) &&
               !(moduleType instanceof DoxygenTypeFile)) {
                continue;
            }

            String packageName = convertAvailableName(moduleType.getName());

            IRPModelElement element = parentPackage.findAllByName(packageName, "Package");
            if( element == null ) {
                debug(parentPackage.getName() + " is not found, Create Package:"+ packageName);
                try {
                    IRPPackage createPackage = parentPackage.addNestedPackage(packageName);
                    createPackage.setSeparateSaveUnit(0);
                    element = createPackage;
                } catch (Exception e) {
                    error("addNestedPackage Error name:"+ packageName, e);
                    element = null;
                }

            } else {
                trace(parentPackage.getName() + " is found "+ packageName); 
            }

            parentPackage = getObject(element);
            if( parentPackage == null) {
                break;
            }
        }
        
        return parentPackage;
    }


    protected String convertAvailableName( String name ) {
        String oldname = new String(name);
        return oldname.trim().replaceAll("\\.|-|\\$", "_").trim();
    }

}