package com.back.boundedContexts.member.domain.shared

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MemberTest {
    @Test
    fun `SYSTEM 회원은 system 고정 정보로 생성된다`() {
        assertThat(Member.SYSTEM.id).isEqualTo(1)
        assertThat(Member.SYSTEM.username).isEqualTo("system")
        assertThat(Member.SYSTEM.nickname).isEqualTo("시스템")
        assertThat(Member.SYSTEM.name).isEqualTo("시스템")
        assertThat(Member.SYSTEM.isAdmin).isTrue()
    }

    @Test
    fun `genApiKey 는 UUID 형식의 문자열을 생성한다`() {
        val apiKey1 = Member.genApiKey()
        val apiKey2 = Member.genApiKey()

        assertThat(apiKey1).matches("^[0-9a-fA-F-]{36}$")
        assertThat(apiKey2).matches("^[0-9a-fA-F-]{36}$")
        assertThat(apiKey1).isNotEqualTo(apiKey2)
    }
}
