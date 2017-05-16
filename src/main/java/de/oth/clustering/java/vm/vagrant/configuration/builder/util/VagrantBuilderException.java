package de.oth.clustering.java.vm.vagrant.configuration.builder.util;

import java.io.Serializable;

/**
 * The default Exception for all builder classes.
 * @author oliver.ziegert
 *
 */
public class VagrantBuilderException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 1L;

	public VagrantBuilderException() {
        super();
    }

    public VagrantBuilderException(String message) {
        super(message);
    }

    public VagrantBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public VagrantBuilderException(Throwable cause) {
        super(cause);
    }
}
