package com.tumoji.tumoji.data.meme.repository;

import com.tumoji.tumoji.data.meme.model.MemeModel;
import com.tumoji.tumoji.data.tag.model.TagModel;
import com.tumoji.tumoji.network.retrofit.APIFactory;
import com.tumoji.tumoji.network.retrofit.MemeAPI;
import com.tumoji.tumoji.storage.sqlite.MemeDatabase;
import com.tumoji.tumoji.utils.Token;

import java.io.File;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: souler
 * Date  : 16-12-14
 *
 * NOTE:
 * Now we have 4 models commonly used over the app: User, Account, Meme, Tag. Account extends User
 * with some extra fields (accessToken, etc.).
 * User and Tag are recommended to be cached in the database. Since Account extends User, we only
 * cache the User that current Account extends, and store other extending fields to
 * SharedPreferences (Well, there should have been a Session model if we allow a user to be signed
 * in on multiple devices. But we simply ignore such case and merge Session model into Account
 * model).
 * After serious thinking, I decided that Meme model lists will not be cached to database. Simply
 * put, the most popular and latest memes updates very frequently, so it becomes meaningless to
 * cache them. However, I plan to add two more features: Downloaded Meme Management and Downloaded
 * Meme Distinguishing. The former requires a SQLite table (says downloaded_memes) which records the
 * meme ID, title, uploader, image size and local filename. These fields don't change frequently and
 * should be accessible offline. The latter requires new boolean field to tell whether this meme is
 * downloaded. I've already updated the MemeModel.
 */
public class MemeRepository implements IMemeRepository {
    // Meme singleton
    private static MemeRepository memeRepository;

    // Api
    private static final MemeAPI memeApi = APIFactory.getMemeAPIInstance();

    // Database
    private static final MemeDatabase memeDatabase = MemeDatabase.getInstance();

    // Need user token to do something
    private Token usingToken;

    // caching temporary memes
    private MemeModel tempMeme;

    public Token getUsingToken() {
        return usingToken;
    }

    public void setUsingToken(Token usingToken) {
        this.usingToken = usingToken;
    }

    public static MemeRepository getInstance() {
        if (memeRepository == null) {
            memeRepository = new MemeRepository();
        }
        return memeRepository;
    }

    private MemeRepository() {
        usingToken = null;
    }

    @Override
    public Observable<MemeModel> likeMeme(String token, String memeId, boolean like) {
        // TODO
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public Observable<MemeModel> reportMeme(String token, String memeId, String reason) {
        // TODO
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public Observable<List<MemeModel>> getMemesList(int offset, int count, TagModel tagModel, int order) {
        // TODO
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public Observable<MemeModel> getMeme(String memeId) {
        // TODO
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public Observable<MemeModel> downloadMeme(String memeId, File destDir) {
        // TODO
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * Like some meme
     * Remember , before calling this function , you need to login first
     * Hence you should pass in token parameter before calling this function
     * @param memeModel The meme model to be liked
     * @param listener  Result callback
     */
    @Override
    public void likeMeme(MemeModel memeModel, OnLikeUnlikeMemeListener listener) {
        if (usingToken == null) {
            listener.onFailure(404 , "You need to login first and set the token parameters");
            return;
        }
        /**
         * Calling api
         */
        memeApi.likeMemeByIdAndToken(memeModel.getMemeId() , usingToken.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(likeRelation -> {
                    // No need to store like relations in local storage???
                    listener.onSuccess(memeModel);
                });
    }

    /**
     * Un-like some meme
     *
     * @param memeModel The meme model to be un-liked
     * @param listener  Result callback
     */
    @Override
    public void unlikeMeme(MemeModel memeModel, OnLikeUnlikeMemeListener listener) {
        if (usingToken == null) {
            listener.onFailure(404 , "You need to login first and set the token parameters");
            return;
        }
        /**
         * Calling api
         */
        memeApi.unlikeMemeByIdAndToken(memeModel.getMemeId() , usingToken.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(errorType -> {
                    // No need to store like relations in local storage???
                    listener.onSuccess(memeModel);
                });
    }

    /**
     * Report some meme
     *
     *  What????
     * @param memeModel The meme model to be reported
     * @param reason    The reason of this report
     * @param listener  Result callback
     */
    @Override
    public void reportMeme(MemeModel memeModel, String reason, OnReportMemeListener listener) {

    }

    /**
     * Get popular memes list from API server
     * NOTE: You should cache the gotten popular memes list to local storage (SQLite, etc.) for the
     * further calls of {@link #getCachedPopularMemesList() getCachedPopularMemesList} method.
     * However, you should only cache them when tag is not specified, because it's impossible to
     * cache memes for every tag.
     *
     *  No this api from server
     *
     * @param offset   The offset of the result list
     * @param tagModel The tag model specifying tag of memes, or null if no tag is specified
     * @param listener Result callback
     */
    @Override
    public void getPopularMemesList(int offset, TagModel tagModel, OnGetMemesListListener listener) {

    }

    /**
     * Get new memes list from API server
     * NOTE: You should cache the gotten new memes list to local storage (SQLite, etc.) for the
     * further calls of {@link #getCachedNewMemesList()} getCachedNewMemesList} method.
     * However, you should only cache them when tag is not specified, because it's impossible to
     * cache memes for every tag.
     *
     * @param offset   The offset of the result list
     * @param tagModel The tag model specifying tag of memes, or null if no tag is specified
     * @param listener Result callback
     */
    @Override
    public void getNewMemesList(int offset, TagModel tagModel, OnGetMemesListListener listener) {

    }

    /**
     * Get cached popular memes list from local storage
     *
     * @return Cached popular memes list
     */
    @Override
    public List<MemeModel> getCachedPopularMemesList() {
        return null;
    }

    /**
     * Get cached new memes list from local storage
     *
     * @return Cached new memes list
     */
    @Override
    public List<MemeModel> getCachedNewMemesList() {
        return null;
    }

    /**
     * @param memeId The meme ID of the meme to get
     * @param listener Callback
     */
    @Override
    public void getMeme(String memeId, OnGetResultListener<MemeModel> listener) {
//        // TODO: Implement getMeme
//        throw new UnsupportedOperationException("Method not implemented");
        memeApi.getMemeById(memeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(memeModel -> {
                   this.tempMeme = memeModel;
                    // store in the database
                    memeDatabase.addMeme(tempMeme);
                    listener.onSuccess(tempMeme);
                });
        listener.onFailure(404 , "Un oh?");
    }

    @Override
    public MemeModel getCachedMeme(String memeId) {
//        // TODO: Implement getCachedMeme
//        throw new UnsupportedOperationException("Method not implemented");
        return memeDatabase.getMemeByName(memeId);
    }

    @Override
    public void saveMeme(MemeModel memeModel, com.tumoji.tumoji.common.OnGetResultListener<MemeModel> listener) {
        // TODO
        throw new UnsupportedOperationException("Method not implemented");
    }
}
