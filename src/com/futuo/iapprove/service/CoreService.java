package com.futuo.iapprove.service;

import java.util.Timer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.futuo.iapprove.R;

public class CoreService extends Service {

	private static final String LOG_TAG = CoreService.class.getCanonicalName();

	// core service local binder
	private IBinder _mLocalBinder;

	// core service work timer
	private final Timer CORESERVICE_WORK_TIMER = new Timer();

	// milliseconds per minute
	private final Long MILLISECONDS_PER_MINUTE = 60 * 1000L;

	// core service task and period array
	private final Object[][] CORESERVICE_TASK7PERIODS = new Object[][] {
			{ GetUserEnterpriseTodoListTaskTask.class,
					new GetUserEnterpriseTodoListTaskTask(),
					R.integer.cstp_getUserEnterpriseTodoListTask_period },
			{ GetEnterpriseABTask.class, new GetEnterpriseABTask(),
					R.integer.cstp_getEnterpriseAB_period },
			{ GetEnterpriseFormTask.class, new GetEnterpriseFormTask(),
					R.integer.cstp_getEnterpriseForm_period } };

	@Override
	public void onCreate() {
		super.onCreate();

		// initialize core service local binder
		_mLocalBinder = new LocalBinder();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// start core service timer
		startCoreServiceTimer();

		// we want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
		// return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "Bind core service using local binder");

		return _mLocalBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);

		Log.d(LOG_TAG, "Rebind core service");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(LOG_TAG, "Unbind core service");

		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// free core service local binder
		_mLocalBinder = null;

		// stop core service timer
		stopCoreServiceTimer();
	}

	// start get user enterprise to-do list task
	public void startGetUserEnterpriseTodoListTask() {
		Log.d(LOG_TAG, "Start get user enterprise to-do list task");

		// get and check get user enterprise to-do list task task
		CoreServiceTask _getUserEnterpriseTodoListTaskTask = getCoreServiceTask(GetUserEnterpriseTodoListTaskTask.class);
		if (null != _getUserEnterpriseTodoListTaskTask) {
			// set enterprise id
			_getUserEnterpriseTodoListTaskTask.setEnterpriseId();
		}
	}

	// force get user enterprise to-do list task
	public void forceGetUserEnterpriseTodoListTask() {
		Log.d(LOG_TAG, "Force get user enterprise to-do list task");

		// get and check get user enterprise to-do list task task
		CoreServiceTask _getUserEnterpriseTodoListTaskTask = getCoreServiceTask(GetUserEnterpriseTodoListTaskTask.class);
		if (null != _getUserEnterpriseTodoListTaskTask) {
			// set enterprise id
			_getUserEnterpriseTodoListTaskTask.setEnterpriseId();

			// force get user enterprise to-do list task immediately
			_getUserEnterpriseTodoListTaskTask.execute();
		}
	}

	// stop get user enterprise to-do list task
	public void stopGetUserEnterpriseTodolistTask() {
		Log.d(LOG_TAG, "Stop get user enterprise to-do list task");

		// get and check get user enterprise to-do list task task
		CoreServiceTask _getUserEnterpriseTodoListTaskTask = getCoreServiceTask(GetUserEnterpriseTodoListTaskTask.class);
		if (null != _getUserEnterpriseTodoListTaskTask) {
			// clear enterprise id
			_getUserEnterpriseTodoListTaskTask.clearEnterpriseId();
		}
	}

	// start get enterprise address book
	public void startGetEnterpriseAddressbook() {
		Log.d(LOG_TAG, "Start get enterprise address book");

		// get and check get enterprise address book task
		CoreServiceTask _getEnterpriseABTask = getCoreServiceTask(GetEnterpriseABTask.class);
		if (null != _getEnterpriseABTask) {
			// set enterprise id
			_getEnterpriseABTask.setEnterpriseId();
		}
	}

	// force get enterprise address book
	public void forceGetEnterpriseAddressbook() {
		Log.d(LOG_TAG, "Force get enterprise address book");

		// get and check get enterprise address book task
		CoreServiceTask _getEnterpriseABTask = getCoreServiceTask(GetEnterpriseABTask.class);
		if (null != _getEnterpriseABTask) {
			// set enterprise id
			_getEnterpriseABTask.setEnterpriseId();

			// force get enterprise address book immediately
			_getEnterpriseABTask.execute();
		}
	}

	// stop get enterprise address book
	public void stopGetEnterpriseAddressbook() {
		Log.d(LOG_TAG, "Stop get enterprise address book");

		// get and check get enterprise address book task
		CoreServiceTask _getEnterpriseABTask = getCoreServiceTask(GetEnterpriseABTask.class);
		if (null != _getEnterpriseABTask) {
			// clear enterprise id
			_getEnterpriseABTask.clearEnterpriseId();
		}
	}

	// start get enterprise form
	public void startGetEnterpriseForm() {
		Log.d(LOG_TAG, "Start get enterprise form");

		// get and check get enterprise form task
		CoreServiceTask _getEnterpriseFormTask = getCoreServiceTask(GetEnterpriseFormTask.class);
		if (null != _getEnterpriseFormTask) {
			// set enterprise id
			_getEnterpriseFormTask.setEnterpriseId();
		}
	}

	// force get enterprise form type
	public void forceGetEnterpriseFormType() {
		Log.d(LOG_TAG, "Force get enterprise form type");

		// get and check get enterprise form task
		CoreServiceTask _getEnterpriseFormTask = getCoreServiceTask(GetEnterpriseFormTask.class);
		if (null != _getEnterpriseFormTask) {
			// set enterprise id
			_getEnterpriseFormTask.setEnterpriseId();
		}
	}

	// force get enterprise form
	public void forceGetEnterpriseForm(Long formTypeId) {
		Log.d(LOG_TAG, "Force get enterprise form");

		// get and check get enterprise form task
		GetEnterpriseFormTask _getEnterpriseFormTask = (GetEnterpriseFormTask) getCoreServiceTask(GetEnterpriseFormTask.class);
		if (null != _getEnterpriseFormTask) {
			// get enterprise form with form type id
			_getEnterpriseFormTask.getEnterpriseFormWithTypeId(formTypeId);
		}
	}

	// stop get enterprise form
	public void stopGetEnterpriseForm() {
		Log.d(LOG_TAG, "Stop get enterprise form");

		// get and check get enterprise form task
		CoreServiceTask _getEnterpriseFormTask = getCoreServiceTask(GetEnterpriseFormTask.class);
		if (null != _getEnterpriseFormTask) {
			// clear enterprise id
			_getEnterpriseFormTask.clearEnterpriseId();
		}
	}

	// start core service timer
	private void startCoreServiceTimer() {
		// start core service task one by one
		for (int i = 0; i < CORESERVICE_TASK7PERIODS.length; i++) {
			try {
				// get core service task and period
				// core service task
				CoreServiceTask _coreServiceTask = (CoreServiceTask) CORESERVICE_TASK7PERIODS[i][1];

				// core service task period
				Long _coreServiceTaskPeriod = MILLISECONDS_PER_MINUTE
						* getResources().getInteger(
								(Integer) CORESERVICE_TASK7PERIODS[i][2]);

				// start the core service task, get immediately and then get
				// again after period time
				CORESERVICE_WORK_TIMER.schedule(_coreServiceTask, 0,
						_coreServiceTaskPeriod);

				Log.d(LOG_TAG, "Start the core service task = "
						+ _coreServiceTask + " and period = "
						+ _coreServiceTaskPeriod);
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Start the core service task error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}
	}

	// stop core service timer
	private void stopCoreServiceTimer() {
		// stop core service task one by one
		for (int i = 0; i < CORESERVICE_TASK7PERIODS.length; i++) {
			// get core service task
			CoreServiceTask _coreServiceTask = (CoreServiceTask) CORESERVICE_TASK7PERIODS[i][1];

			// cancel the core service task
			_coreServiceTask.cancel();
		}

		// cancel work timer
		CORESERVICE_WORK_TIMER.cancel();
	}

	// get core service task by class
	private CoreServiceTask getCoreServiceTask(
			Class<? extends CoreServiceTask> coreServiceTaskCls) {
		CoreServiceTask _coreServiceTask = null;

		for (int i = 0; i < CORESERVICE_TASK7PERIODS.length; i++) {
			// get core service task class and object
			// core service task class
			@SuppressWarnings("unchecked")
			Class<CoreServiceTask> _coreServiceTaskCls = (Class<CoreServiceTask>) CORESERVICE_TASK7PERIODS[i][0];

			// check core service task class
			if (_coreServiceTaskCls.equals(coreServiceTaskCls)) {
				_coreServiceTask = (CoreServiceTask) CORESERVICE_TASK7PERIODS[i][1];

				// break immediately
				break;
			}
		}

		return _coreServiceTask;
	}

	// inner class
	// local binder
	public class LocalBinder extends Binder {

		// get service
		public CoreService getService() {
			return CoreService.this;
		}

	}

}
