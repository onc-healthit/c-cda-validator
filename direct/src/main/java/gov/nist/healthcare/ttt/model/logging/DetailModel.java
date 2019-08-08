package gov.nist.healthcare.ttt.model.logging;

import gov.nist.healthcare.ttt.database.log.DetailImpl;
import gov.nist.healthcare.ttt.database.log.DetailInterface;

public class DetailModel extends DetailImpl implements DetailInterface {
	
	public DetailModel(String dts, String name, String found, String expected,
			String rfc, Status status) {
		super();
		this.setDts(dts);
		this.setName(name);
		this.setFound(found);
		this.setExpected(expected);
		this.setRfc(rfc);
		this.setStatus(status);
	}
	
	public boolean isSuccess() {
		if(this.getStatus().equals(Status.ERROR)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "["
//				+ "name=" + name 
				+ "dts=" + this.getDts() 
//				+ ", found=" + found 
//				+ ", expected=" + expected 
//				+ ", rfc=" + rfc
//				+ ", status=" + status 
				+ "]\n";
	}

}
