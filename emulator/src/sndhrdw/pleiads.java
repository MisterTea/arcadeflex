/*
This file is part of Arcadeflex.

Arcadeflex is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Arcadeflex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Arcadeflex.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * ported to v0.28
 * ported to v0.27
 *
 *
 *
 */
package sndhrdw;

import static arcadeflex.libc.*;
import static arcadeflex.osdepend.*;
import static mame.driverH.*;
import static mame.mame.*;

public class pleiads 
{

       static final int SAFREQ=1400;
       static final int SBFREQ=1400;
       static final int MAXFREQ_A=44100*7;
       static final int MAXFREQ_B1=44100*4;
       static final int MAXFREQ_B2=44100*4;
       
        /* for voice A effects */
        static final int SW_INTERVAL=4;
        static final double MOD_RATE=0.14;
        static final double MOD_DEPTH=0.1;
        /* for voice B effect */
        static final double SWEEP_RATE=0.14;
        static final double SWEEP_DEPTH=0.24;

        /* values needed by phoenix_sh_update */
        static int sound_a_play = 0;
        static int sound_a_vol = 0;
        static int sound_a_freq = SAFREQ;
        static int sound_a_adjust=1;
        static double x;

        static int sound_b1_play = 0;
        static int sound_b1_vol = 0;
        static int sound_b1_freq = SBFREQ;
        static int sound_b2_play = 0;
        static int sound_b2_vol = 0;
        static int sound_b2_freq = SBFREQ;
        static int sound_b_adjust=1;

        static int noise_vol = 0;
        static int noise_freq = 1000;
        static int pitch_a = 0;
        static int pitch_b1 = 0;
        static int pitch_b2 = 0;

        static int noisemulate=0;
        static int portBstatus=0;
    
	public static ShInitPtr pleiads_sh_init = new ShInitPtr() { public int handler(String gamename)
	{
            x = Math.PI/2;

            if (Machine.samples != null && Machine.samples.sample[0] != null)    /* We should check also that Samplename[0] = 0 */
              noisemulate = 0;
            else
              noisemulate = 1;

        return 0;
	} };
	
	 static int lastnoise;
	public static WriteHandlerPtr pleiads_sound_control_a_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                   

                /* voice a */
                int freq = data & 0x0f;
                int vol = (data & 0x30) >> 4;

                /* noise */
                int noise = (data & 0xc0) >> 6;

                if (freq != sound_a_freq) sound_a_adjust = 1;
                else sound_a_adjust=0;

                sound_a_freq = freq;
                sound_a_vol = vol;

                if (freq != 0x0f)
                {
                        osd_adjust_sample(0,MAXFREQ_A/(16-sound_a_freq),85*(3-vol));
                        sound_a_play = 1;
                }
                else
                {
                        osd_adjust_sample(0,SAFREQ,0);
                        sound_a_play = 0;
                }

                if (noisemulate!=0) {
                   if (noise_freq != 1750*(4-noise))
                   {
                           noise_freq = 1750*(4-noise);
                           noise_vol = 85*noise;
                   }

                   if (noise!=0) osd_adjust_sample(3,noise_freq,noise_vol);
                   else
                   {
                           osd_adjust_sample(3,1000,0);
                           noise_vol = 0;
                   }
                 }
                else
                 {
                   switch (noise) {
                     case 1 : if (lastnoise != noise)
                                osd_play_sample(3,Machine.samples.sample[0].data,
                                                  Machine.samples.sample[0].length,
                                                  Machine.samples.sample[0].smpfreq,
                                                  Machine.samples.sample[0].volume,0);
                              break;
                     case 2 : if (lastnoise != noise)
                                osd_play_sample(3,Machine.samples.sample[1].data,
                                                  Machine.samples.sample[1].length,
                                                  Machine.samples.sample[1].smpfreq,
                                                  Machine.samples.sample[1].volume,0);
                              break;
                   }
                   lastnoise = noise;
                 }
                if (errorlog!=null) fprintf(errorlog,"A:%X ",new Object[] { Integer.valueOf(data)});
	} };
	
	
	
	public static WriteHandlerPtr pleiads_sound_control_b_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                if (portBstatus==0) {
                /* voice b1 */
                int freq = data & 0x0f;
                int vol = (data & 0x30) >> 4;

                /* melody - osd_play_midi anyone? */
                /* 0 - no tune, 1 - alarm beep?, 2 - even level tune, 3 - odd level tune */
                /*int tune = (data & 0xc0) >> 6;*/

                if (freq != sound_b1_freq) sound_b_adjust = 1;
                else sound_b_adjust=0;

                sound_b1_freq = freq;
                sound_b1_vol = vol;

                if (freq != 0x0f)
                {
                        osd_adjust_sample(1,MAXFREQ_B1/(16-sound_b1_freq),85*(3-vol));
                        sound_b1_play = 1;
                }
                else
                {
                        osd_adjust_sample(1,SBFREQ,0);
                        sound_b1_play = 0;
                }
           }

        else {
                /* voice b2 */
                int freq = data & 0x0f;
                int vol = (data & 0x30) >> 4;

                /* melody - osd_play_midi anyone? */
                /* 0 - no tune, 1 - alarm beep?, 2 - even level tune, 3 - odd level tune */
                /*int tune = (data & 0xc0) >> 6;*/

                if (freq != sound_b2_freq) sound_b_adjust = 1;
                else sound_b_adjust=0;

                sound_b2_freq = freq;
                sound_b2_vol = vol;

                if (freq != 0x0f)
                {
                        osd_adjust_sample(2,MAXFREQ_B2/(16-sound_b2_freq),85*(3-vol));
                        sound_b2_play = 1;
                }
                else
                {
                        osd_adjust_sample(2,SBFREQ,0);
                        sound_b2_play = 0;
                }
           }

             portBstatus=NOT(portBstatus);
                if (errorlog!=null) fprintf(errorlog,"B:%X ",new Object[] { Integer.valueOf(data)});
	} };
	
	
	
	public static ShStartPtr pleiads_sh_start = new ShStartPtr() { public int handler()
	{
		osd_play_sample(0,Machine.drv.samples,32,1000,0,1);
                osd_play_sample(1,Machine.drv.samples,32,1000,0,1);
                osd_play_sample(2,Machine.drv.samples,32,1000,0,1);
                osd_play_sample(3,Machine.drv.samples,128,1000,0,1);
                return 0;
	} };
	
	
	
	public static ShUpdatePtr pleiads_sh_update = new ShUpdatePtr() { public void handler()
	{
	        pitch_a=MAXFREQ_A/(16-sound_a_freq);
                pitch_b1=MAXFREQ_B1/(16-sound_b1_freq);
                pitch_b2=MAXFREQ_B2/(16-sound_b2_freq);
        /*
                if (hifreq)
                    pitch_a=pitch_a*5/4;

                pitch_a+=((double)pitch_a*MOD_DEPTH*sin(t));

                sound_a_sw++;

                if (sound_a_sw==SW_INTERVAL)
                {
                    hifreq=!hifreq;
                    sound_a_sw=0;
                }

                t+=MOD_RATE;

                if (t>2*PI)
                    t=0;

                pitch_b+=((double)pitch_b*SWEEP_DEPTH*sin(x));

        if (sound_b_vol==3 || (last_b_freq==15&&sound_b_freq==12) || (last_b_freq!=14&&sound_b_freq==6))
                    x=0;

                x+=SWEEP_RATE;

                if (x>3*PI/2)
                    x=3*PI/2;

          */

                if (sound_a_play!=0)
                        osd_adjust_sample(0,pitch_a,85*(3-sound_a_vol));
                if (sound_b1_play!=0)
                        osd_adjust_sample(1,pitch_b1,85*(3-sound_b1_vol));
                if (sound_b2_play!=0)
                        osd_adjust_sample(2,pitch_b2,85*(3-sound_b2_vol));

                if ((noise_vol!=0) && (noisemulate!=0))
                {
                        osd_adjust_sample(3,noise_freq,noise_vol);
                        noise_vol-=3;
                }
	} }; 
}
