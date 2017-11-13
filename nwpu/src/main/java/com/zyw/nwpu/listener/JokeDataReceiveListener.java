package com.zyw.nwpu.listener;

import java.util.ArrayList;

import com.zyw.nwpulib.model.JokeEntity;

public interface JokeDataReceiveListener {

	public void OnJokeDataReceived(ArrayList<JokeEntity> jokelist,
			boolean loadmore);
}
