// IRemoteService.aidl
package com.tcl.wechat.xmpp;

import com.tcl.wechat.xmpp.ICallback;


interface IRemoteService {   
 /** Request the process ID of this service, to do evil things with it. */  
 
   void registerCallback(ICallback cb);
    
   void unregisterCallback();
  
}