package de.jotschi.vertx.ferma.ext.orientdb;

@FunctionalInterface
public interface AsyncTxHandler<E> {

	void handle(E event) throws Exception;
}
