package com.gpuimage.appdemo.fragment.testcase;

import android.Manifest;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.gpuimage.appdemo.R;
import com.gpuimage.appdemo.base.BaseFragment;
import com.gpuimage.appdemo.camera.view.CameraView;
import com.gpuimage.appdemo.model.ItemDescription;
import com.gpuimage.appdemo.utils.LogUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;

/**
 * @ClassName
 * @Description
 * @Author danny
 * @Date 2017/12/20 11:26
 */
public class TestCameraV1ReaderFragment extends BaseFragment {
    protected static final String TAG = TestCameraV1ReaderFragment.class.getSimpleName();
    @BindView(R.id.topbar) QMUITopBar mTopBar;
    @BindView(R.id.camera) protected CameraView mCameraView;
    @BindView(R.id.take_picture) protected Button mTakePicBtn;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_test_camera_reader, null);
        ButterKnife.bind(this, root);
        initTopBar();
        onCreateViewCompleted();
        LogUtil.v(TAG, "onCreateView");
        return root;
    }

    @NeedsPermission({Manifest.permission.CAMERA})
    public void onCreateViewCompleted() {
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    public void onPause() {
        mCameraView.stop();
        super.onPause();
    }


    @OnClick(R.id.take_picture )
    public void openCamera(){
         mCameraView.start();
    }

    public static ItemDescription mItemDescription = new ItemDescription(TestCameraV1ReaderFragment.class,
            "非eglcore的camera", R.mipmap.icon_tabbar_lab);

    protected ItemDescription getQDItemDescription() {
        return mItemDescription;
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle(getQDItemDescription().getName());
    }

}