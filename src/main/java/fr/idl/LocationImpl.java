package main.java.fr.idl;

import main.java.fr.idl.CodeSearchEngine.Location;
import main.java.fr.idl.CodeSearchEngine.Type;

public class LocationImpl implements Location {

	String path ;
	
	public LocationImpl(String path) {
		this.path = path ;
	}
	@Override
	public String getFilePath() {
		return this.path ;
	}

	@Override
	public int getLineNumber() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean equals(Object obj) {
		Location other = (LocationImpl) obj;

		return this.path.equals(other.getFilePath());
	}
}
