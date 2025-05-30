package io.github.dhslrl321.oassample

import io.github.dhslrl321.api.controller.*
import io.github.dhslrl321.api.model.*
import org.springframework.http.ResponseEntity

class InternalController: InternalApi {
  override fun internalSampleGet(): ResponseEntity<InternalSampleGet200Response> {
    TODO("Not yet implemented")
  }
}

class ExternalController: ExternalApi {
  override fun externalSampleGet(): ResponseEntity<ExternalSampleGet200Response> {
    TODO("Not yet implemented")
  }
}
