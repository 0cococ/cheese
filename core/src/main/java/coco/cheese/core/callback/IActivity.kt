package coco.cheese.core.callback

import androidx.appcompat.app.AppCompatActivity
import coco.cheese.core.activity.StubEnv

interface IActivity {
     fun onCreate(stubEnv: StubEnv)
}