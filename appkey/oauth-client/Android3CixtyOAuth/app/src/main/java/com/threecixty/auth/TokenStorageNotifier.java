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

import android.content.Context;

/**
 * This class is used to notify OAuthManager about a new token info received.
 *
 */
public class TokenStorageNotifier {

	private Context context;

	protected TokenStorageNotifier(Context context) {
		this.context = context;
	}

	protected void notifyToken(TokenInfo tokenInfo, String appid) {
		OAuthManager.getInstance().saveTokenInfo(context, tokenInfo, appid);
	}

	protected void deleteToken(String appid) {
		OAuthManager.getInstance().deleteTokenInfo(context, appid);
	}
}
