package com.example.practicaltest021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PracticalTest02MainActivity extends Activity {
	private EditText op1Text, op2Text;
	private TextView add_result_text, mul_result_text, serverPo;
	private ServerThread serverThread;
	int serverPorttt = 3001;
	
	private class ServerThread extends Thread {
		private boolean isRunning;
		private ServerSocket serverSocket;
	
		public void startServer() {
			isRunning = true;
			start();
			System.out.println("Server started");
			
		}
		
		
		public void stopServer() {
			isRunning = false;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					if (serverSocket != null)
						try {
							serverSocket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}).start();
		}
		
		public void run() {
			int serverPort = serverPorttt;
			try {
				serverSocket = new ServerSocket(serverPort);
				while (isRunning) {
					Socket socket = serverSocket.accept();
					new CommunicationThread(socket).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class CommunicationThread extends Thread {
		private Socket socket;
		
		public CommunicationThread(Socket socket) {
			this.socket = socket;
			System.out.println("CommThread with new socket");
		}
		
		public void run () {
			try {
				PrintWriter writer = Utilities.getWriter(socket);
				BufferedReader reader = Utilities.getReader(socket);
				String data = reader.readLine();
				System.out.println(data);
				// TODO
				String delim = ",";
				String ops[] = data.split(delim);
				int result = 0;
				if (ops[0].compareTo("add") == 0)
				{
					result = Integer.parseInt(ops[1]) + Integer.parseInt(ops[2]);
					
				} else {
					result = Integer.parseInt(ops[1]) * Integer.parseInt(ops[2]);
					this.sleep(1000);
				}
				
				String new_data = String.valueOf(result);				
				writer.println(new_data);
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class ClientThread extends Thread {
		private Socket socket = null;
		private int port;
		private String ip, data;
		
		public ClientThread(String data) {
			this.ip = "localhost";
			this.port = serverPorttt;
			this.data = data;
		}
		
		public void run() {
			try {
				socket = new Socket(ip, port);
				BufferedReader reader = Utilities.getReader(socket);
				PrintWriter writer = Utilities.getWriter(socket);
				
				writer.println(data);
				writer.flush();
				String response;
				while ((response = reader.readLine()) != null) {
					System.out.println(response);
				}
				final String st = response;
				add_result_text.post(new Runnable() {
					
					@Override
					public void run() {
						add_result_text.setText(st);
						
					}
				});
				
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				socket.close();
				System.out.println("Client socket closed");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practical_test02_main);
		
		serverThread = new ServerThread();
		serverThread.startServer();
	
		
		op1Text = (EditText)findViewById(R.id.op1);
		op2Text = (EditText)findViewById(R.id.op2);
		add_result_text = (EditText)findViewById(R.id.add_result);
		mul_result_text = (EditText)findViewById(R.id.mul_result);
		serverPo = (TextView)findViewById(R.id.server_port);
		serverPo.setText(String.valueOf(serverPorttt));
		
		Button add = (Button)findViewById(R.id.add);
		Button mul = (Button)findViewById(R.id.mul);
		
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String data = "add," + op1Text.getText().toString() + "," + op2Text.getText().toString() + "\n";
				new ClientThread(data).start();
			}
		});
		
		mul.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String data = "mul," + op1Text.getText().toString() + "," + op2Text.getText().toString() + "\n";
				new ClientThread(data).start();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.practical_test02_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
