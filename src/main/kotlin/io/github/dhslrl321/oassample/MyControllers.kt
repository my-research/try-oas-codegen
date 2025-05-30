package io.github.dhslrl321.oassample

import kr.co.uplus.ixiosearch.api.controller.ExternalApi
import kr.co.uplus.ixiosearch.api.controller.InternalApi
import kr.co.uplus.ixiosearch.api.model.ExternalSampleGet200Response
import kr.co.uplus.ixiosearch.api.model.InternalSampleGet200Response
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
