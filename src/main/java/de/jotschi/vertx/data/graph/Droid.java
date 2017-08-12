package de.jotschi.vertx.data.graph;

import com.syncleus.ferma.annotations.GraphElement;

@GraphElement
public class Droid extends MovieCharacter {

	public static final String FUNCTION = "primaryFunction";

	public void setPrimaryFunction(String functionName) {
		setProperty(FUNCTION, functionName);
	}

	public String getPrimaryFunction() {
		return getProperty(FUNCTION);
	}

}
