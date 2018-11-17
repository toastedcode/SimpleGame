package com.toast.game.engine.interfaces;

import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public interface Syncable
{
   public boolean isChanged();
   
   public XmlNode syncTo(XmlNode node);
   
   public void syncFrom(XmlNode node) throws XmlFormatException;
}
