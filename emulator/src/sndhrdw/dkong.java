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

package sndhrdw;
/*
 * ported to v0.28
 * ported to v0.27
 *
 *  TODO :fix functions if we support external samples
 */
import static mame.driverH.*;
import static mame.mame.*;

public class dkong
{

	public static WriteHandlerPtr dkong_sh1_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	
	
		if (Machine.samples == null) return;
            /*    static state[8];  //TODO if we support external samples


                if (Machine->samples == 0) return;

                if (state[offset] != data)
                {
                        if ((Machine->samples->sample[offset] != 0) && (data))
                                osd_play_sample(offset,Machine->samples->sample[offset]->data,
                                                Machine->samples->sample[offset]->length,
                                                Machine->samples->sample[offset]->smpfreq,
                                                Machine->samples->sample[offset]->volume,0);

                        state[offset] = data;
                }*/
	
	} };
	
	
	public static WriteHandlerPtr dkong_sh3_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	
	
		if (Machine.samples == null) return;
                	

        /*static last_dead;//TODO if we support external samples
	if (Machine->samples == 0) return;

                if (last_dead != data)
                {
                        last_dead = data;
                        if (data)
                        {
                                if (Machine->samples->sample[24])
                                        osd_play_sample(0,Machine->samples->sample[24]->data,
                                                        Machine->samples->sample[24]->length,
                                                        Machine->samples->sample[24]->smpfreq,
                                                        Machine->samples->sample[24]->volume,0);
                                osd_stop_sample(1);		  /* kill other samples */
                /*                osd_stop_sample(2);
                                osd_stop_sample(3);
                                osd_stop_sample(4);
                                osd_stop_sample(5);
                        }
                }*/
	
	} };
	
	
	public static WriteHandlerPtr dkong_sh2_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	
		if (Machine.samples == null) return;
     /*           	static last;//TODO if we support external samples
                        static hit = 0;

                        if (Machine->samples == 0) return;

                    if (data != 6)
                        {
                           hit = 0;
                    }

                        if (last != data)
                        {
                                last = data;

                                switch (data)
                                {
                                   case 6:	  /* mix in hammer hit on different channel */
    /*                               last = 4;
                                   if (!hit)
                                   {
                                       if (Machine->samples->sample[data+8] != 0)
                                           {
                                                  osd_play_sample(3,Machine->samples->sample[data+8]->data,
                                                     Machine->samples->sample[data+8]->length,
                                                     Machine->samples->sample[data+8]->smpfreq,
                                                     Machine->samples->sample[data+8]->volume,0);
                                           }
                                           hit = 1;
                                        }
                                        break;

                            case 13:
                                        last = 11;
                                    if (Machine->samples->sample[data+8] != 0)
                                        {
                                           osd_play_sample(3,Machine->samples->sample[data+8]->data,
                                                  Machine->samples->sample[data+8]->length,
                                                  Machine->samples->sample[data+8]->smpfreq,
                                                  Machine->samples->sample[data+8]->volume,0);
                                        }
                                        break;

                                        case 8:		  /* these samples repeat */
  /*                                      case 9:
                                        case 10:
                                        case 11:
                                        case 4:
                                        case 3:
                                        if (Machine->samples->sample[data+8] != 0)
                                        {
                                                osd_play_sample(4,Machine->samples->sample[data+8]->data,
                                                                Machine->samples->sample[data+8]->length,
                                                                Machine->samples->sample[data+8]->smpfreq,
                                                                Machine->samples->sample[data+8]->volume,1);
                                        }
                                        break;

                                        default:		  /* the rest do not */
   /*                                     if (data != 0)
                                        {
                                                if (Machine->samples->sample[data+8] != 0)
                                                {
                                                        osd_play_sample(4,Machine->samples->sample[data+8]->data,
                                                                        Machine->samples->sample[data+8]->length,
                                                                        Machine->samples->sample[data+8]->smpfreq,
                                                                        Machine->samples->sample[data+8]->volume,0);

                                                }
                                        }
                                        break;
                                }
                        }*/
        
	
	} };
	
	
	
	public static ShUpdatePtr dkong_sh_update = new ShUpdatePtr() { public void handler()
	{
	} };
}
