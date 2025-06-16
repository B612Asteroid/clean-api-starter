package kr.co.chunjae.aidtlcms.user.token

/**
 * Token
 *
 * @constructor Create empty Token
 */
class Token {
    var grantType: String? = null
    var accessToken: String? = null
    var refreshToken: String? = null

    constructor(grantType: String?, accessToken: String?, refreshToken: String?) {
        this.grantType = grantType
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    constructor()
}
