package coco.cheese.core.aidl.client;

interface IKeyboardClient {
    void input(String text);
    void delete();
    void enter();
}