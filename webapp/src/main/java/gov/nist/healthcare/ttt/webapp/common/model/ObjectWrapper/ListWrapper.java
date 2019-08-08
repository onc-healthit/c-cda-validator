package gov.nist.healthcare.ttt.webapp.common.model.ObjectWrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ListWrapper<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<T> content;

	@SafeVarargs
	public ListWrapper(T... objects) {
		this.content = Arrays.asList(objects);
	}
	
	public ListWrapper(Collection<T> col) {
		this.content = new ArrayList<T>(col);
	}

	public ListWrapper(List<T> content) {
		this.content = content;
	}

	public List<T> getContent() {
		return content;
	}

}
