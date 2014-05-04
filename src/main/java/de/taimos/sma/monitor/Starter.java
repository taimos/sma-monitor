package de.taimos.sma.monitor;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;

import de.taimos.sma.energymeter.SMAData;
import de.taimos.sma.energymeter.SMAField;
import de.taimos.sma.energymeter.SMAReader;
import de.taimos.sma.energymeter.SMAReader.SMACallback;
import eu.hansolo.steelseries.gauges.Linear;
import eu.hansolo.steelseries.tools.ColorDef;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 *
 */
public class Starter extends Frame {

	private static final long serialVersionUID = 1L;
	
	private Linear powerTotal;
	private Linear powerL1;
	private Linear powerL2;
	private Linear powerL3;
	
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Starter s = new Starter();
	}

	public Starter() {
		this.setTitle("SMA Reader");
		this.setSize(800, 600);
		this.setLayout(new GridLayout(4, 1));
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		SMACallback cb = new SMACallback() {

			@Override
			public void timeout() {
				//
			}

			@Override
			public void error(Exception err) {
				err.printStackTrace();
			}

			@Override
			public void dataReceived(SMAData data) {
				Starter.this.setTitle("SMA Reader - " + data.getSerial());

				this.setValue(data, Starter.this.powerTotal, SMAField.TOTAL_P_IN, SMAField.TOTAL_P_OUT);
				this.setValue(data, Starter.this.powerL1, SMAField.L1_P_IN, SMAField.L1_P_OUT);
				this.setValue(data, Starter.this.powerL2, SMAField.L2_P_IN, SMAField.L2_P_OUT);
				this.setValue(data, Starter.this.powerL3, SMAField.L3_P_IN, SMAField.L3_P_OUT);
			}
			
			private void setValue(SMAData data, Linear lin, SMAField in, SMAField out) {
				final BigDecimal value;
				if (data.getValue(in).signum() > 0) {
					value = data.getValue(in);
					lin.setValueColor(ColorDef.RED);
				} else {
					value = data.getValue(out);
					lin.setValueColor(ColorDef.GREEN);
				}
				if (value != null) {
					lin.setValue(value.doubleValue());
				}
			}
		};
		SMAReader reader = new SMAReader(cb);

		this.powerTotal = this.createLinear("Total Power", SMAField.TOTAL_P_IN);
		this.powerL1 = this.createLinear("L1 Power", SMAField.L1_P_IN);
		this.powerL2 = this.createLinear("L2 Power", SMAField.L2_P_IN);
		this.powerL3 = this.createLinear("L3 Power", SMAField.L3_P_IN);

		this.add(this.powerTotal);
		this.add(this.powerL1);
		this.add(this.powerL2);
		this.add(this.powerL3);

		this.setVisible(true);
		reader.start();
	}

	private Linear createLinear(String title, SMAField field) {
		Linear l = new Linear();
		l.setMinValue(0);
		l.setMaxValue(5000);
		l.setTitle(title);
		l.setLcdVisible(true);
		l.setLcdUnitString(field.getUnit());
		l.setLcdUnitStringVisible(true);
		l.setUnitString(field.getUnit());
		l.setUnitStringVisible(true);
		l.setUserLedBlinking(false);
		return l;
	}
}
