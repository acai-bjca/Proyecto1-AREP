/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author estudiante
 */
public class Service {
    public static HashMap<String, Handler> urlsHandler = new HashMap<String, Handler>();
    
    public static void init(){
        
        try {
            Class<?> clase = Class.forName("WebServiceHello");
            Method squareM = clase.getDeclaredMethod("square", null);
            squareM.invoke(null, null);
            
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    public static void addMethod(String className){
        Class<?> clase;
        try {
            clase = Class.forName(className);
            Method[] methods = clase.getMethods();
            for(Method method : methods){
                if(method.isAnnotationPresent(Web.class)){
                   urlsHandler.put("apps/"+method.getAnnotation(Web.class), new StaticMethodHandler(method));
                }
               
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
    }
    
    public static void listen(){
    
    }
    
    
}
