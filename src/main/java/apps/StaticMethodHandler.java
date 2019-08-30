/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author estudiante
 */
public class StaticMethodHandler implements Handler{
    public Method method;            

    StaticMethodHandler(Method method) {
        this.method = method;
    }
            
    public String process() {
        try {
            return method.invoke(null, null).toString();
        } catch (IllegalAccessException ex) {
            Logger.getLogger(StaticMethodHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(StaticMethodHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(StaticMethodHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
