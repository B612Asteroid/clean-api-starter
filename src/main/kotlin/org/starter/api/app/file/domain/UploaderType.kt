package org.starter.api.app.file.domain

import org.starter.api.infra.jpa.CodeEnum
import org.starter.api.infra.jpa.CodeEnumConverter

/**
 * 업로드 파일의 종류
 */
enum class UploaderType(override val code: String, override val label: String) : CodeEnum {

    DEFAULT("FI", "파일"),  // default
    MEDIA("ME", "미디어"),  // 미디어
    SCREENSHOT("SC", "스크린샷"),
    AUDIO("AU", "오디오"),
    IMAGE("IM", "이미지"),
    THUMBNAIL("TH", "썸네일"),  // 썸네일
    PDF("PD", "PDF"),
    VIDEO("PL", ""),
    HTML("HT", "HTML"),
    ZIP("ZI", "ZIP"),
    VTT("SM", "자막"),  // 자막
    SCRIPT("SF", "대본"),  // 대본
    LINK("LI", "링크"),
    FOLDER("FO", "폴더");


    /**
     * 내부 타입핸들러 클래스. 마이바티스와 연동하기 위해선 다음 코드를 반드시 구현해야함
     */
    class Converter : CodeEnumConverter<UploaderType>(UploaderType::class.java)
}