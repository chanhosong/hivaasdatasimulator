/**
 * 
 */
package com.hhi.vaas.platform.vdm.handler;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.hhi.vaas.platform.middleware.common.util.XMLUtil;

/**
 * @author BongJin Kwon
 *
 */
public class VDMPath {
	
	private String systemName;
	private String vdmpath;
	private String lDeviceInst;
	private String lnPath;
	private String doName;
	private String daName;
	private String sdoName;
	private String bdaName;
	
	private List<String> pathList;
	
	public VDMPath(String systemName, String vdmpath) {
		
		this.systemName = systemName;
		this.vdmpath = vdmpath;
		
		parseVDMPath();
	}
	
	private void parseVDMPath(){
		
		pathList = new ArrayList<String>();
		
		// hsbae_150510 : if vdmpath == null then it is system node. not error
		if(vdmpath == null || vdmpath.trim().length() == 0){
			//throw new VDMException("Invalid vdmpath: vdmpath is null or blank.");
			this.vdmpath = null;
			return;
		}
		
		int pos = vdmpath.indexOf(VDMConstants.DELIMITER1);
		
		if(pos < 1){
			lDeviceInst = vdmpath;
			pathList.add(lDeviceInst);
			
		}else{
			lDeviceInst = vdmpath.substring(0, pos);
			pathList.add(lDeviceInst);
			
			String[] paths = vdmpath.substring(pos+1).split("\\.");
			
			for (int i = 0; i < paths.length; i++) {
				
				pathList.add(paths[i]);
				
				switch (i) {
				case 0:
					lnPath = paths[i];
					break;
				case 1:
					doName = paths[i];
					break;
				case 2:
					daName = paths[i];
					break;
				case 3:
					bdaName = paths[i];
					break;
				case 4:
					sdoName = paths[i-2];
					daName = paths[i-1];
					bdaName = paths[i];
					break;
				default:
					break;
				}
			}
		}
	}

	public String getSystemName() {
		return systemName;
	}

	/**
	 * 
	 * @return GPS of GPS/GPS1.Pos.latitude.deg
	 */
	public String getlDeviceInst() {
		return lDeviceInst;
	}

	/**
	 * 
	 * @return GPS1(prefix + lnClass + inst) of GPS/GPS1.Pos.latitude.deg
	 */
	public String getLnPath() {
		return lnPath;
	}

	/**
	 * 
	 * @return Pos of GPS/GPS1.Pos.latitude.deg
	 */
	public String getDoName() {
		return doName;
	}
	
	public String getSdoName() {
		return sdoName;
	}

	public void setSdoName(String sdoName) {
		this.sdoName = sdoName;
	}

	/**
	 * 
	 * @return latitude of GPS/GPS1.Pos.latitude.deg
	 */
	public String getDaName() {
		return daName;
	}

	public void setDaName(String daName) {
		this.daName = daName;
	}

	/**
	 * 
	 * @return deg of GPS/GPS1.Pos.latitude.deg
	 */
	public String getBdaName() {
		return bdaName;
	}
	
	public String getVdmpath() {
		return vdmpath;
	}
	
	/**
	 * one based depth : start 1.
	 * @return
	 */
	public int getDepth(){
		return pathList.size();
	}
	
	/**
	 * return depth path.
	 * @param depth one based depth : start 1.
	 * @return depth path
	 */
	public String getPath(int depth){
		if(getDepth() < depth){
			return null;
		}
		return pathList.get(depth-1);
	}
	
	/**
	 * return next vdmpath delimeter ('/' or '.')
	 * @return
	 */
	public String getNextDelimiter(){
		if(getDepth() == 1){
			return VDMConstants.DELIMITER1;
		}
		return VDMConstants.DELIMITER2;
	}
	
	public String getNextPath(Node childNode){
		if(getDepth() == 1){
			return getLNPath(childNode);
		}
		return XMLUtil.getAttribute(childNode, VDMConstants.NODE_ATTR_NAME);
	}

	@Override
	public String toString() {
		return "VDMPath [vdmpath=" + vdmpath + "]";
	}
	
	public static String getLNPath(Node lnNode){
		
		String[] attrs = XMLUtil.getAttributes(lnNode, "prefix", "lnClass", "inst");

		return attrs[0] + attrs[1] + attrs[2];
		
	}
	
	public String getLastPath(){
		return getPath(getDepth());
	}
	
	
}
