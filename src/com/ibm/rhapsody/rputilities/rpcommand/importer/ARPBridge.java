package com.ibm.rhapsody.rputilities.rpcommand.importer;

import java.util.List;

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
    protected final String ELEMENT_NAME_CHANGE_PREFIX = "Changed";
    protected final String ELEMENT_NAME_DELETE_PREFIX = "Deleted";

    DoxygenType doxygen_ = null;
    IRPPackage rootPackage_ = null;

    protected ARPBridge(Class<?> clazz, DoxygenType doxygen, IRPPackage rootPackage) {
        super(clazz);
        doxygen_ = doxygen;
        rootPackage_ = rootPackage;

    }

    abstract public IRPModelElement searchElementByType(IRPPackage rpPackage);
    abstract public IRPModelElement createElementByType(IRPPackage modulPackage);
    abstract public boolean isUpdate(IRPModelElement element);
    abstract public void applyByType(IRPModelElement element, String currentVersion);


    public IRPModelElement importElement(String currentVersion) {
        IRPModelElement current_element = null;
        IRPModelElement unavailable_element = null; 

        boolean update = false;
        current_element = searchElement(rootPackage_);
        if( current_element != null ) {
            
            String baseVersion = GetBaseVersion(current_element);
            update = isUpdate(current_element);
            if( update == true && baseVersion.equals(currentVersion) != true) {
                unavailable_element = current_element;
                current_element = null;
            }
        }

        IRPPackage versionPackage = createVersionPackage(currentVersion);
        IRPPackage modulePackage = CreateModulePackage(versionPackage);

        if( current_element != null ) {
            apply(current_element, modulePackage, currentVersion);
            return current_element;
        }
        
        current_element = createElementByType(modulePackage);
        if(current_element == null) {
            return null;
        }

        apply(current_element, modulePackage, currentVersion);

        if(unavailable_element != null) {
            unavailableElement(unavailable_element, current_element, currentVersion);
        }

        return current_element;
    }

    public IRPModelElement searchElement(IRPPackage rpPackage) {
        if(rpPackage == null) { 
            return null;
        }

        IRPModelElement element = searchElementByType(rpPackage);

        if(element != null) {
            return element;
        }
        
        List<IRPPackage> packages = toList(rpPackage.getPackages());

        for(IRPPackage subPackage :  packages) {
            element = searchElement(subPackage);
            if(element != null) {
                return element;
            }
        }

        return null;
    }

    public IRPModelElement createElement(IRPPackage versionPackage) {
        IRPPackage targetPackage = CreateModulePackage(versionPackage);
        IRPModelElement element = createElementByType(targetPackage); 
        return element;
    }

    public void apply(IRPModelElement element, IRPPackage modulePackage, String currentVersion) {

        updateOwner(element,modulePackage);
        setApplicableVersion(element, currentVersion);
        applyByType(element, currentVersion);
    }

    protected void updateOwner(IRPModelElement element, IRPPackage modulePackage) {
        IRPPackage ownerPackage = getPackage(element);
        String ownerID = "";
        String ownerName = "";
        if(getPackage(element) != null) {
            ownerID = ownerPackage.getGUID();
            ownerName = ownerPackage.getName();
        }
        if( ownerID.equals(modulePackage.getGUID()) != true ) {
            debug(String.format("element:%s's owner %s(%s) set owner:%s(%s)",
                    element.getName(),
                    ownerName,
                    ownerID,
                    modulePackage.getName(),
                    modulePackage.getGUID()));
            element.setOwner(modulePackage);
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


    protected void unavailableElement(IRPModelElement unavailable, IRPModelElement current, String currentVersion) {
        setUnabavailableVersion(unavailable,currentVersion);

        String change_name = String.format("%s_%s_%s", 
                                ELEMENT_NAME_CHANGE_PREFIX,
                                convertAvailableName(currentVersion),
                                unavailable.getName());
        
        unavailable.setName(change_name);

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

    public String GetBaseVersion(IRPModelElement rpelement) {
        String baseVersion = "";
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

    public IRPPackage CreateModulePackage(IRPPackage versionPackage) {
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