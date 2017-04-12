package webui.messages

import play.api.libs.json.{Format, Reads, Writes}
import org.apache.commons.codec.binary.Base64

/**
  * Created by mischcon on 10.04.17.
  */
trait WebUIMessages

case class UploadJar(content : Array[Byte])

//object UploadJar //{
//  implicit val MessageFormat: Format[UploadJar] =
//    Format(Reads.of[String].map(s => apply(Base64.decodeBase64(s))),
//      Writes(a => Writes.of[String].writes(Base64.encodeBase64String(a.content))))
//}