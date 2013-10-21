package main.java.fr.idl;

import main.java.fr.idl.CodeSearchEngine.Location;

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

}
