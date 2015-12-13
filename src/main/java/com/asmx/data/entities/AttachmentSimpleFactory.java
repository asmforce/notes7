package com.asmx.data.entities;

/**
 * User: asmforce
 * Timestamp: 29.11.15 15:12.
**/
public class AttachmentSimpleFactory implements AttachmentFactory {
    @Override
    public Attachment create() {
        return new AttachmentSimple();
    }
}
