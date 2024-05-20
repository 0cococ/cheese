package coco.cheese.core.aidl.type

import android.os.Parcel
import android.os.Parcelable

class NodeType() : Parcelable {
    var _text: String? = null
    var _id: String? = null
    var _desc: String? = null
    var _clz: String? = null
    var _pkg: String? = null
    var _bounds: String? = null
    var _isClickable: String? = null
    var and = false
    var or = false
    var on = 0

    constructor(parcel: Parcel) : this() {
        readFromParcel(parcel)
    }

    private fun readFromParcel(parcel: Parcel) {
        this._text = parcel.readString();
        this._id = parcel.readString();
        this._desc = parcel.readString();
        this._clz = parcel.readString();
        this._pkg = parcel.readString();
        this._bounds = parcel.readString();
        this._isClickable= parcel.readString();
    }

    fun text(e: String): NodeType {
        if (_text == e) {
            if (on != 1) {
                and = true
                on = 0
            }
            or = true
        } else {
            on = 1
            and = false
        }
        return this
    }

    fun id(e: String): NodeType {
        if (_id == e) {
            if (on != 1) {
                and = true
                on = 0
            }
            or = true
        } else {
            on = 1
            and = false
        }
        return this
    }

    fun desc(e: String): NodeType {
        if (_desc == e) {
            if (on != 1) {
                and = true
                on = 0
            }
            or = true
        } else {
            on = 1
            and = false
        }
        return this
    }

    fun clz(e: String): NodeType {
        if (_clz == e) {
            if (on != 1) {
                and = true
                on = 0
            }
            or = true
        } else {
            on = 1
            and = false
        }
        return this
    }

    fun pkg(e: String): NodeType {
        if (_pkg == e) {
            if (on != 1) {
                and = true
                on = 0
            }
            or = true
        } else {
            on = 1
            and = false
        }
        return this
    }

    fun bounds(e: String): NodeType {
        if (_bounds == e) {
            if (on != 1) {
                and = true
                on = 0
            }
            or = true
        } else {
            on = 1
            and = false
        }
        return this
    }

    fun isClickable(e: Boolean): NodeType {
        if (_isClickable == e.toString()) {
            if (on != 1) {
                and = true
                on = 0
            }
            or = true
        } else {
            on = 1
            and = false
        }
        return this
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this._text);
        parcel.writeString(this._id);
        parcel.writeString(this._desc);
        parcel.writeString(this._clz);
        parcel.writeString(this._pkg);
        parcel.writeString(this._bounds);
        parcel.writeString(this._isClickable);
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NodeType> {
        override fun createFromParcel(parcel: Parcel): NodeType {
            return NodeType(parcel)
        }

        override fun newArray(size: Int): Array<NodeType?> {
            return arrayOfNulls(size)
        }
    }
}