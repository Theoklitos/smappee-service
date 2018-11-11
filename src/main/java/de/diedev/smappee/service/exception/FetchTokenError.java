package de.diedev.smappee.service.exception;

public class FetchTokenError extends Error {

	private static final long serialVersionUID = 3817852849243458343L;

	public FetchTokenError(final Exception e) {
		super(e);
	}

	public FetchTokenError(final String message) {
		super(message);
	}
}
