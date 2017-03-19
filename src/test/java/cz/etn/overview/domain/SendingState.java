/*
 * Created on 22. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.domain;

/**
 * State of message sending.
 * @author Radek Beran
 */
public enum SendingState {
	/** Sending is forbidden/stopped. */
	FORBIDDEN,
	/** Ready to send. */
	READY,
	/** Already sent. */
	SENT
}
