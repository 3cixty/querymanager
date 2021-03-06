/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package com.threecixty.auth;

/**
 * Callback to notify about responses received from 3Cixty OAuth server.
 *
 */
public interface OAuthCallback {

	/**
	 * This method is to notify about 3Cixty access token.
	 * @param tokenInfo
	 */
	void notify3CixtyAccessToken(TokenInfo tokenInfo);

	void notifyTokenRevoked(String token, boolean tokenRevoked);
}
