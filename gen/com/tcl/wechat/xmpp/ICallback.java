/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\Project\\work\\WeChat_bate_2.5\\src\\com\\tcl\\wechat\\xmpp\\ICallback.aidl
 */
package com.tcl.wechat.xmpp;
public interface ICallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.tcl.wechat.xmpp.ICallback
{
private static final java.lang.String DESCRIPTOR = "com.tcl.wechat.xmpp.ICallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.tcl.wechat.xmpp.ICallback interface,
 * generating a proxy if needed.
 */
public static com.tcl.wechat.xmpp.ICallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.tcl.wechat.xmpp.ICallback))) {
return ((com.tcl.wechat.xmpp.ICallback)iin);
}
return new com.tcl.wechat.xmpp.ICallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setConnection:
{
data.enforceInterface(DESCRIPTOR);
com.tcl.wechat.xmpp.WeiConnection _arg0;
if ((0!=data.readInt())) {
_arg0 = com.tcl.wechat.xmpp.WeiConnection.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.setConnection(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.tcl.wechat.xmpp.ICallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void setConnection(com.tcl.wechat.xmpp.WeiConnection weiConnection) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((weiConnection!=null)) {
_data.writeInt(1);
weiConnection.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_setConnection, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_setConnection = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void setConnection(com.tcl.wechat.xmpp.WeiConnection weiConnection) throws android.os.RemoteException;
}
