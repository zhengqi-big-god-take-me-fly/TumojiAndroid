package com.tumoji.tumoji.memes.presenter;

import com.tumoji.tumoji.common.OnGetResultListener;
import com.tumoji.tumoji.data.auth.repository.IAuthRepository;
import com.tumoji.tumoji.data.meme.model.MemeModel;
import com.tumoji.tumoji.data.meme.repository.IMemeRepository;
import com.tumoji.tumoji.data.settings.repository.ISettingsRepository;
import com.tumoji.tumoji.data.tag.repository.ITagRepository;
import com.tumoji.tumoji.data.user.repository.IUserRepository;
import com.tumoji.tumoji.memes.contract.MemeDetailContract;

import java.io.File;

/**
 * Author: perqin
 * Date  : 12/17/16
 */

public class MemeDetailPresenter implements MemeDetailContract.Presenter {
    private IMemeRepository mMemeRepository;
    private ITagRepository mTagRepository;
    private IUserRepository mUserRepository;
    private ISettingsRepository mSettingsRepository;
    private IAuthRepository mAuthRepository;
    private MemeDetailContract.View mView;

    public MemeDetailPresenter(IAuthRepository authRepository, IMemeRepository memeRepository, ITagRepository tagRepository, IUserRepository userRepository, ISettingsRepository settingsRepository, MemeDetailContract.View view) {
        this.mAuthRepository = authRepository;
        this.mMemeRepository = memeRepository;
        this.mTagRepository = tagRepository;
        this.mUserRepository = userRepository;
        this.mSettingsRepository = settingsRepository;
        this.mView = view;
    }

    @Override
    public void init(String memeId) {
        File parentDir = new File(mSettingsRepository.getSettingString(ISettingsRepository.PK_SETTINGS_MEME_DIRECTORY));
        // Refresh meme image, title, likes, reports, downloaded
        mMemeRepository.getMeme(parentDir, memeId).subscribe(memeModel -> {
            mView.refreshMemeDetail(memeModel);
        }, throwable -> {
            // TODO
            throw new UnsupportedOperationException("Method not implemented");
        });
        // Refresh author
        mUserRepository.getMemeAuthor(memeId).subscribe(userModel -> {
            mView.refreshMemeAuthor(userModel);
        }, throwable -> {
            // TODO
            throw new UnsupportedOperationException("Method not implemented");
        });
        // Refresh tags
        mTagRepository.getTagsListOfMeme(memeId).subscribe(tagModels -> {
            mView.refreshMemeTags(tagModels);
        }, throwable -> {
            // TODO
            throw new UnsupportedOperationException("Method not implemented");
        });
    }

    @Override
    public void likeMeme(MemeModel memeModel) {
        if (mAuthRepository.isSignedIn()) {
            mMemeRepository.likeMeme(mAuthRepository.getLocalAuth().getAccessToken(), memeModel.getMemeId(), true).subscribe(aVoid -> {
                mView.refreshMemeDetail(memeModel.withLiked(true));
            }, throwable -> {
                // TODO
                throw new UnsupportedOperationException("Method not implemented");
            });
        } else {
            mView.showUnSignedInError();
        }
    }

    @Override
    public void unlikeMeme(MemeModel memeModel) {
        if (mAuthRepository.isSignedIn()) {
            mMemeRepository.likeMeme(mAuthRepository.getLocalAuth().getAccessToken(), memeModel.getMemeId(), false).subscribe(aVoid -> {
                mView.refreshMemeDetail(memeModel.withLiked(false));
            }, throwable -> {
                // TODO
                throw new UnsupportedOperationException("Method not implemented");
            });
        } else {
            mView.showUnSignedInError();
        }
    }

    @Override
    public void reportMeme(MemeModel memeModel, String reason) {
    }

    @Override
    public void saveMeme(MemeModel mMemeModel) {
        mMemeRepository.saveMeme(mMemeModel, new OnGetResultListener<MemeModel>() {
            @Override
            public void onSuccess(MemeModel result) {
                mView.refreshMemeDetail(result);
            }

            @Override
            public void onFailure(int error, String msg) {
                // TODO
                throw new UnsupportedOperationException("Method not implemented");
            }
        });
    }
}
