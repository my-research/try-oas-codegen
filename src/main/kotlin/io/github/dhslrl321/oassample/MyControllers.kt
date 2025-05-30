package io.github.dhslrl321.oassample

import io.github.dhslrl321.api.controller.*
import io.github.dhslrl321.api.model.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class InternalController: InternalApi {
  override fun internalSampleGet(): ResponseEntity<InternalSampleGet200Response> {
    return ResponseEntity.ok(InternalSampleGet200Response(message = "hello this is internal"))
  }
}

@RestController
class ExternalController: ExternalApi {
  override fun externalSampleGet(): ResponseEntity<ExternalSampleGet200Response> {
    return ResponseEntity.ok(ExternalSampleGet200Response(message = "hello this is external"))
  }
}
