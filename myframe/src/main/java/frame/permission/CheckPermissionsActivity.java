package frame.permission; /**
 * 
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.example.myframe.R;

import java.util.ArrayList;
import java.util.List;


/**
 *　　┏┓　　  ┏┓+ +
 *　┏┛┻━ ━ ━┛┻┓ + +
 *　┃　　　　　　  ┃
 *　┃　　　━　　    ┃ ++ + + +
 *     ████━████     ┃+
 *　┃　　　　　　  ┃ +
 *　┃　　　┻　　  ┃
 *　┃　　　　　　  ┃ + +
 *　┗━┓　　　┏━┛
 *　　　┃　　　┃　　　　　　　　　　　
 *　　　┃　　　┃ + + + +
 *　　　┃　　　┃
 *　　　┃　　　┃ +  神兽保佑
 *　　　┃　　　┃    代码无bug！　
 *　　　┃　　　┃　　+　　　　　　　　　
 *　　　┃　 　　┗━━━┓ + +
 *　　　┃ 　　　　　　　┣┓
 *　　　┃ 　　　　　　　┏┛
 *　　　┗┓┓┏━┳┓┏┛ + + + +
 *　　　　┃┫┫　┃┫┫
 *　　　　┗┻┛　┗┻┛+ + + +
 * ━━━━━━神兽出没━━━━━━
 * Author：LvQingYang
 * Date：2017/3/25
 * Email：biloba12345@gamil.com
 * Info：继承了Activity，实现Android6.0的运行时权限检测
 * 需要进行运行时权限检测的Activity可以继承这个类
 */


@SuppressLint("Registered")
public abstract class CheckPermissionsActivity extends AppCompatActivity
		implements
			ActivityCompat.OnRequestPermissionsResultCallback {
	
	private static final int PERMISSON_REQUESTCODE = 0;
	
	/**
	 * 判断是否需要检测，防止不停的弹框
	 */
	private boolean isNeedCheck = true;

	protected void checkPermissions() {
		if (isNeedCheck) {
			List<String> needRequestPermissonList = findDeniedPermissions(getNeedPermissions());
			if (null != needRequestPermissonList
					&& needRequestPermissonList.size() > 0) {
				ActivityCompat.requestPermissions(this,
						needRequestPermissonList.toArray(
								new String[needRequestPermissonList.size()]),
						PERMISSON_REQUESTCODE);
			}
		}
	}

	protected boolean isNeedPermission(){
		return findDeniedPermissions(getNeedPermissions()).size()>0;
	}

	/**
	 * 获取权限集中需要申请权限的列表
	 * 
	 * @param permissions
	 * @return
	 * @since 2.5.0
	 *
	 */
	private List<String> findDeniedPermissions(String[] permissions) {
		List<String> needRequestPermissonList = new ArrayList<String>();
		for (String perm : permissions) {
			if (ContextCompat.checkSelfPermission(this,
					perm) != PackageManager.PERMISSION_GRANTED
					|| ActivityCompat.shouldShowRequestPermissionRationale(
							this, perm)) {
				needRequestPermissonList.add(perm);
			} 
		}
		return needRequestPermissonList;
	}

	/**
	 * 检测是否说有的权限都已经授权
	 * @param grantResults
	 * @return
	 * @since 2.5.0
	 *
	 */
	private boolean verifyPermissions(int[] grantResults) {
		for (int result : grantResults) {
			if (result != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
		if (requestCode == PERMISSON_REQUESTCODE) {
			if (!verifyPermissions(paramArrayOfInt)) {
				showMissingPermissionDialog();
				isNeedCheck = false;
			}
		}
	}

	/**
	 * 显示提示信息
	 * 
	 * @since 2.5.0
	 *
	 */
	private void showMissingPermissionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.notifyTitle);
		builder.setMessage(R.string.notifyMsg);

		// 拒绝, 退出应用
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});

		builder.setPositiveButton(R.string.setting,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startAppSettings();
					}
				});

		builder.setCancelable(false);

		builder.show();
	}

	/**
	 *  启动应用的设置
	 * 
	 * @since 2.5.0
	 *
	 */
	private void startAppSettings() {
		Intent intent = new Intent(
				Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + getPackageName()));
		startActivity(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected abstract String[] getNeedPermissions();
		
}
