/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1201.recruitment.controller;

/**
 *  Thrown when return type is not sufficient to specify
 * what went wrong during an operation. Thrown when a verification 
 * of sorts has failed unexpectedly
 * @author Perttu Jääskeläinen
 */
public class VerificationFailedException extends Exception {
    public VerificationFailedException() {
        
    }
    public VerificationFailedException(String msg) {
        super(msg);
    }
}
