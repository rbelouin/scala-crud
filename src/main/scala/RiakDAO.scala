package com.rbelouin.scalacrud

import dispatch._

import net.liftweb.json._

import scalaz._
import scalaz.Scalaz._

trait RiakDAOComponentImpl[A <: AnyRef] extends DAOComponent[A] {
  case class RiakDAO[A <: AnyRef](host: String, port: Int, bucket: String)(implicit val mf: Manifest[A])  extends DAO[A] {
    implicit val formats = DefaultFormats
    val map = scala.collection.mutable.Map.empty[String,A]

    override def get(id: String) = {
      val req = :/(host, port) / "buckets" / bucket / "keys" / id
      Http(req > (r => for {
        res <- Option(r)
        if res.getStatusCode < 300
        json <- JsonParser.parseOpt(res.getResponseBody)
        a <- json.extractOpt[A]
      } yield a))()
    }
    override def update(id: String, a: A) = {
      val req = :/(host, port) / "buckets" / bucket / "keys" / id <:< Map("Content-Type" -> "application/json") << Serialization.write(a)
      Http(req.PUT > (r => Option(a).filter(x => r.getStatusCode < 300)))()
    }
    override def remove(id: String) = {
      val req = :/(host, port) / "buckets" / bucket / "keys" / id
      Http(req.DELETE > (r => Option(r).filter(_.getStatusCode < 300).map(_ => ())))()
    }
    
    override def getAll = {
      val req = :/(host, port) / "mapred" <:< Map("Content-Type" -> "application/json") << """
        {
          "inputs": "%s",
          "query": [{
            "map": {
              "language": "javascript",
              "source": "function(x) {
                var a = {};
                a[x.key] = JSON.parse(x.values[0].data);
                return a;
              }"
            }
          }]
        }
      """.format(bucket)

      def res2map(r: com.ning.http.client.Response) = (for {
        res <- Option(r)
        if res.getStatusCode < 300
        json <- JsonParser.parseOpt(res.getResponseBody)
        aa <- json.extractOpt[Map[String,A]]
      } yield aa).getOrElse(Map.empty[String,A])

      Http(req > (r => {
        res2map(r)
      }))()
    }
    override def removeAll = {
      val req = :/(host, port) / "buckets" / bucket / "keys" <<? Map("keys" -> "true")

      def res2keys(r: com.ning.http.client.Response) = (for {
        res <- Option(r)
        if res.getStatusCode < 300
        json <- JsonParser.parseOpt(res.getResponseBody)
        kk <- (json \ "keys").extractOpt[List[String]]
      } yield kk) | Nil

      Http(req > (res2keys _))().map(remove _)
    }
    override def create(a: A) = {
      val req = :/(host, port) / "buckets" / bucket / "keys" <:< Map("Content-Type" -> "application/json") << Serialization.write(a)
      Http(req > (r => Option(r.getHeader("Location")).flatMap(_.split("/").lastOption).getOrElse("")))()
    }
  }
}
