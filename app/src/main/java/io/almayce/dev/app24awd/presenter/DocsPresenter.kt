package io.almayce.dev.app24awd.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.almayce.dev.app24awd.global.BitmapManager
import io.almayce.dev.app24awd.global.SchedulersTransformer
import io.almayce.dev.app24awd.global.Serializer
import io.almayce.dev.app24awd.model.docs.Doc
import io.almayce.dev.app24awd.model.docs.DocList
import io.almayce.dev.app24awd.view.docs.DocsView

/**
 * Created by almayce on 30.10.17.
 */
@InjectViewState
class DocsPresenter : MvpPresenter<DocsView>() {

    var bm = BitmapManager()

    init {
        bm.onTransformedBitmapObservable
                .compose(SchedulersTransformer())
                .subscribe({ it ->
                    viewState.notifyDataSetChanged()
                })
    }

    fun addDoc(doc: Doc) {
        DocList.add(doc)
        serialize()
        viewState.notifyDataSetChanged()
    }

    fun editDoc(position: Int, doc: Doc) {
        DocList.set(position, doc)
        serialize()
        viewState.notifyDataSetChanged()
    }

    fun removeDoc(position: Int) {
        DocList.removeAt(position)
        serialize()
        viewState.notifyDataSetChanged()
    }

    fun getCurrentDoc(position: Int): Doc =
            DocList.get(position)

    fun serialize() {
        Serializer.serialize(Serializer.FileName.DOCS)
    }

    fun transformBitmap(path: String) {
        bm.transformBitmap(path)
    }
}