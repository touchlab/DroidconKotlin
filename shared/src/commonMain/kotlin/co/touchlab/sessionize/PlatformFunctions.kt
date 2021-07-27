package co.touchlab.sessionize

typealias StaticFileLoader = (String, String) -> String?
typealias ClLogCallback = (String) -> Unit
typealias SoftExceptionCallback = (Throwable, String) -> Unit