package de.beinlich.markus.pizzaservice.util;

import java.util.Map;
import java.util.HashMap;
import javax.servlet.annotation.WebListener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Speichert aktive (gültige) Sessions
 */
//@WebListener
public class ActiveSessionsListener implements HttpSessionListener {

   /**
    * Enthält die aktiven Sessions <Session ID, Session>
    */
   private static Map<String, HttpSession> activeSessions =new HashMap<String, HttpSession>();
   //private static HttpSession[] activeSessionsArray =new HttpSession[28];
   /**
    * Wird gerufen, wenn eine Session erzeugt wurde
    */
   @Override
   public void sessionCreated(HttpSessionEvent event) {
      // Ermitteln der Session ID
       
      String sessionId = event.getSession().getId();

      // Debug-Ausgabe auf der Kommandozeile
      
      System.out.println("Session: " + sessionId + " erzeugt.");
      
      // Speichern der Session in den aktiven Sessions
      activeSessions.put(sessionId, event.getSession());
   }

   /**
    * Wird gerufen, wenn eine Session verfällt oder auch zerstört
    */
   @Override
   public void sessionDestroyed(HttpSessionEvent event) {
      // Ermitteln der Session ID
      String sessionId = event.getSession().getId();

      // Debug-Ausgabe auf der Kommandozeile
      System.out.println("Session: " + sessionId + " verfallen.");

      // Entfernen der Session aus den aktiven Sessions
      activeSessions.remove(sessionId);
   }

   /**
    * Gibt die Liste der aktiven Sessions zur�ck
    */
   public  static Map<String, HttpSession> getActiveSessions() {
      return activeSessions;
   }
}
