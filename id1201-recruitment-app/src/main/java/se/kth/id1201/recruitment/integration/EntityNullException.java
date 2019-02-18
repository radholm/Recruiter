/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.integration;

/**
 *  Exception thrown when an entity is unexpectedly not found in the database
 * @author Perttu Jääskeläinen
 */
public class EntityNullException extends Exception {

    public EntityNullException() {
    }
    
    public EntityNullException(String msg) {
        super(msg);
    }
    
}
