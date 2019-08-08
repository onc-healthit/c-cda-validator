package gov.nist.healthcare.ttt.webapp.common.model.ObjectWrapper;

import java.io.Serializable;

public class ObjWrapper<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private T result;

	public ObjWrapper(T result) {
		this.result = result;
	}

	public T getResult() {
		return result;
	}

}
