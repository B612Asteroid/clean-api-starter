package org.starter.api.core.security

/**
 * Token
 *
 * @constructor Create empty Token
 */
class Token(private var grantType: String?, private var accessToken: String?, private var refreshToken: String?)
