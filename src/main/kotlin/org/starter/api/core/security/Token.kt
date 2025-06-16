package kr.co.chunjae.aidtlcms.user.token

/**
 * @author suhans
 * @email lsuhan00@chunjae.co.kr
 * @create date 2023-12-11 14:04:3451
 * @modify date 2023-12-11 14:04:345
 * @desc 토큰
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
