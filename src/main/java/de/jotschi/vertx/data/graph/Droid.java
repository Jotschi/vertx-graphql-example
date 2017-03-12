package de.jotschi.vertx.data.graph;

import com.gentics.ferma.annotation.GraphElement;

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
