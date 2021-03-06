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
 *  uses romset from v0.36
 */
package drivers;
import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static sndhrdw.generic.*;
import static vidhrdw.sonson.*;
import static m6809.M6809H.*;
import static m6809.M6809.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.capcom.*;
import static mame.memoryH.*;

public class sonson {
      public static InitMachinePtr sonson_init_machine = new InitMachinePtr()
      {
        public void handler() {
            /* Set optimization flags for M6809 */
            m6809_Flags = M6809_FAST_OP | M6809_FAST_S | M6809_FAST_U;

        }
      };
      
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x17ff, MRA_RAM ),
                new MemoryReadAddress( 0x4000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( 0x3002, 0x3002, input_port_0_r ),	/* IN0 */
                new MemoryReadAddress( 0x3003, 0x3003, input_port_1_r ),	/* IN1 */
                new MemoryReadAddress( 0x3004, 0x3004, input_port_2_r ),	/* IN2 */
                new MemoryReadAddress( 0x3005, 0x3005, input_port_3_r ),	/* DSW0 */
                new MemoryReadAddress( 0x3006, 0x3006, input_port_4_r ),	/* DSW1 */
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x0fff, MWA_RAM ),
                new MemoryWriteAddress( 0x1000, 0x13ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x1400, 0x17ff, colorram_w, colorram ),
                new MemoryWriteAddress( 0x2020, 0x207f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x3000, 0x3000, MWA_RAM, sonson_scrollx ),
                new MemoryWriteAddress( 0x3008, 0x3008, MWA_NOP ),
                new MemoryWriteAddress( 0x3010, 0x3010, sound_command_w ),
                new MemoryWriteAddress( 0x3008, 0x3019, MWA_NOP ),
                new MemoryWriteAddress( 0x4000, 0xffff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };

        static MemoryReadAddress sound_readmem[] =
        {
                new MemoryReadAddress( 0x0001, 0x0001, sound_pending_commands_r ),
                new MemoryReadAddress( 0x0002, 0x0002, sound_command_latch_r ),
                new MemoryReadAddress( 0x0000, 0x07ff, MRA_RAM ),
                new MemoryReadAddress( 0xe000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };

        static MemoryWriteAddress sound_writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x07ff, MWA_RAM ),
                new MemoryWriteAddress( 0x2000, 0x2000, AY8910_control_port_0_w ),
                new MemoryWriteAddress( 0x2001, 0x2001, AY8910_write_port_0_w ),
                new MemoryWriteAddress( 0x4000, 0x4000, AY8910_control_port_1_w ),
                new MemoryWriteAddress( 0x4001, 0x4001, AY8910_write_port_1_w ),
                new MemoryWriteAddress( 0xe000, 0xffff, MWA_ROM ),
                new MemoryWriteAddress( -1 )  /* end of table */
        };

        static InputPortPtr input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably unused */
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably unused */
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably unused */

                PORT_START();	/* IN1 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN) ;	/* probably unused */
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER2 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER2 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER2 );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably unused */
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably unused */

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably unused */
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably unused */
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably unused */
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably unused */

                PORT_START();	/* DSW0 */
                PORT_DIPNAME( 0x0f, 0x0f, "Coinage", IP_KEY_NONE );
                PORT_DIPSETTING(    0x02, "4 Coins/1 Credit" );
                PORT_DIPSETTING(    0x05, "3 Coins/1 Credit" );
                PORT_DIPSETTING(    0x08, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x04, "3 Coins/2 Credits" );
                PORT_DIPSETTING(    0x01, "4 Coins/3 Credits" );
                PORT_DIPSETTING(    0x0f, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x03, "3 Coins/4 Credits" );
                PORT_DIPSETTING(    0x07, "2 Coins/3 Credits" );
                PORT_DIPSETTING(    0x0e, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0x06, "2 Coins/5 Credits" );
                PORT_DIPSETTING(    0x0d, "1 Coin/3 Credits" );
                PORT_DIPSETTING(    0x0c, "1 Coin/4 Credits" );
                PORT_DIPSETTING(    0x0b, "1 Coin/5 Credits" );
                PORT_DIPSETTING(    0x0a, "1 Coin/6 Credits" );
                PORT_DIPSETTING(    0x09, "1 Coin/7 Credits" );
                PORT_DIPSETTING(    0x00, "Free Play" );
                PORT_DIPNAME( 0x10, 0x10, "Coinage affects", IP_KEY_NONE );
                PORT_DIPSETTING(    0x10, "Coin A" );
                PORT_DIPSETTING(    0x00, "Coin B" );
                PORT_DIPNAME( 0x20, 0x00, "Demo Sounds", IP_KEY_NONE );
                PORT_DIPSETTING(    0x20, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_BITX(    0x40, 0x40, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x40, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x80, 0x80, "unknown", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );

                PORT_START();	/* DSW1 */
                PORT_DIPNAME( 0x03, 0x03, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x03, "3" );
                PORT_DIPSETTING(    0x02, "4" );
                PORT_DIPSETTING(    0x01, "5" );
                PORT_DIPSETTING(    0x00, "7" );
                PORT_DIPNAME( 0x04, 0x00, "2 Players Game", IP_KEY_NONE );
                PORT_DIPSETTING(    0x04, "1 Credit" );
                PORT_DIPSETTING(    0x00, "2 Credits" );
                PORT_DIPNAME( 0x18, 0x08, "Bonus Life", IP_KEY_NONE );
                PORT_DIPSETTING(    0x08, "20000 80000 100000" );
                PORT_DIPSETTING(    0x00, "30000 90000 120000" );
                PORT_DIPSETTING(    0x18, "20000" );
                PORT_DIPSETTING(    0x10, "30000" );
                PORT_DIPNAME( 0x60, 0x60, "Difficulty", IP_KEY_NONE );
                PORT_DIPSETTING(    0x60, "Easy" );
                PORT_DIPSETTING(    0x40, "Medium" );
                PORT_DIPSETTING(    0x20, "Hard" );
                PORT_DIPSETTING(    0x00, "Hardest" );
                PORT_DIPNAME( 0x80, 0x80, "Freeze", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                INPUT_PORTS_END();
        }};


        static GfxLayout charlayout = new GfxLayout
        (
            	8,8,	/* 8*8 characters */
            1024,	/* 1024 characters */
            2,	/* 2 bits per pixel */
            new int[]{ 1024*8*8, 0 },	/* the two bitplanes are separated */
            new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },	/* pretty straightforward layout */
            new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
            8*8	/* every char takes 8 consecutive bytes */
        );
        static GfxLayout spritelayout = new GfxLayout
        (
                	16,16,	/* 16*16 sprites */
	512,	/* 512 sprites */
	3,	/* 3 bits per pixel */
	new int[]{ 2*2048*8*8, 2048*8*8, 0 },	/* the two bitplanes are separated */
        new int[]{ 8*16+7, 8*16+6, 8*16+5, 8*16+4, 8*16+3, 8*16+2, 8*16+1, 8*16+0,	/* pretty straightforward layout */
	   7, 6, 5, 4, 3, 2, 1, 0 },
	new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
            8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
	32*8	/* every sprite takes 32 consecutive bytes */
        );


        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x00000, charlayout,      0, 64 ),
                new GfxDecodeInfo( 1, 0x04000, spritelayout, 64*4, 32 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        /* these are NOT the original color PROMs */
        static char color_prom[] =
        {
            /* 12: palette red/green component */
            0x00,0x99,0x80,0x32,0x32,0x60,0xBB,0x77,0x46,0x69,0x46,0x68,0xA0,0x9A,0x7C,0x00,
            0x00,0xCC,0x99,0x97,0x00,0x00,0x70,0xB0,0x00,0x09,0x0A,0x7D,0x70,0x80,0xA0,0x11,
            /* 13: palette blue component */
            0x00,0x09,0x00,0x07,0x09,0x0B,0x0B,0x07,0x00,0x04,0x06,0x08,0x0A,0x0A,0x07,0x0C,
            0x00,0x0C,0x09,0x0C,0x0A,0x07,0x0A,0x0D,0x0E,0x08,0x06,0x07,0x0E,0x00,0x00,0x01,
            /* B2: character lookup table */
            0x00,0x9B,0x80,0x30,0x30,0x6C,0xB0,0x70,0x40,0x6D,0x40,0x60,0xA0,0x9E,0x70,0x00,
            0x00,0xCF,0x90,0x90,0x00,0x00,0x70,0xB0,0x00,0x01,0x00,0x70,0x70,0x82,0xA0,0x10,
            0x00,0x03,0x00,0x00,0x00,0x04,0x00,0x00,0x00,0x05,0x00,0x00,0x00,0x06,0x00,0x00,
            0x00,0x07,0x00,0x00,0x00,0x08,0x00,0x00,0x00,0x09,0x00,0x00,0x00,0x0A,0x00,0xD0,
            0xF0,0xF8,0xF9,0xFE,0xF0,0x7A,0xFB,0xFD,0xF0,0xF3,0x74,0xB5,0xF0,0xF7,0xF1,0xB6,
            0xB0,0xF3,0xF4,0xF2,0xF0,0x07,0x00,0x06,0x00,0x02,0x03,0x04,0x00,0x02,0x0A,0x0B,
            0x00,0x09,0x06,0x0E,0x00,0x01,0x06,0x07,0x00,0x07,0x01,0x06,0x00,0x02,0x0C,0x06,
            0x00,0x0C,0x0F,0x02,0x00,0x02,0x0B,0x06,0x00,0x02,0x0F,0xF6,0xF0,0xF2,0xFB,0xFD,
            0xF0,0x72,0xF1,0xF6,0xF0,0xF2,0xF9,0xFE,0xF0,0xF2,0xF4,0xFF,0xB0,0x72,0xF9,0xF6,
            0xD0,0x02,0x0F,0x06,0x00,0x0C,0x03,0x05,0x00,0x01,0x03,0x05,0x00,0x04,0x05,0x06,
            0x00,0x07,0x01,0x05,0x00,0x09,0x0E,0x0C,0x00,0x04,0x0F,0x03,0x00,0x04,0x0F,0x06,
            0x00,0x01,0x05,0x06,0x00,0x04,0x05,0x02,0x00,0x04,0x05,0x0C,0x00,0x0E,0x09,0x06,
            0x00,0x04,0x0F,0xD2,0xF0,0xF4,0xFF,0xF6,0xF0,0x7B,0xFD,0xF6,0xFC,0xFD,0x7E,0x70,
            0xF1,0xF2,0xF3,0xF4,0x75,0xF6,0xF7,0xF8,0xF9,0x0A,0x0B,0x0C,0x0D,0x0E,0x00,0x0F,
            0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x00,0x0F,0x01,
            0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x00,0x0F,0x01,0x02,
            /* B: sprite lookup table */
            0x0F,0x91,0x83,0x34,0x37,0x68,0xBB,0x70,0x4F,0x61,0x43,0x64,0xAC,0x96,0x7B,0x00,
            0x0F,0xC1,0x92,0x94,0x0C,0x08,0x7B,0xB0,0x0F,0x0C,0x04,0x7F,0x7F,0x8F,0xAF,0x10,
            0x0F,0x01,0x02,0x06,0x07,0x0E,0x0B,0x00,0x0F,0x01,0x02,0x08,0x07,0x0B,0x0F,0x00,
            0x0F,0x03,0x06,0x0A,0x0D,0x0E,0x0B,0x00,0x0F,0x01,0x02,0x08,0x07,0x0B,0x0E,0xDC,
            0xFF,0xF1,0xF2,0xF5,0xF8,0x7D,0xFE,0xF0,0xFF,0xF1,0x72,0xBF,0xF7,0xFE,0xFD,0xB0,
            0xBF,0xF1,0xF4,0xF7,0xF8,0x02,0x0C,0x00,0x0F,0x01,0x04,0x07,0x05,0x02,0x0C,0x00,
            0x0F,0x01,0x03,0x04,0x08,0x09,0x06,0x00,0x0F,0x0F,0x0F,0x04,0x08,0x05,0x0F,0x00,
            0x0F,0x05,0x06,0x07,0x0D,0x0E,0x00,0x0F,0x0F,0x01,0x03,0xF4,0xFC,0xFF,0xFB,0xF0,
            0xFF,0x7F,0xF8,0xF4,0xFD,0xFE,0xF0,0xFC,0xFF,0xFF,0xF8,0xF4,0xBD,0x7E,0xF0,0xFC,
            0xDF,0x01,0x06,0x07,0x08,0x0D,0x0C,0x0E,0x0F,0x01,0x03,0x05,0x0D,0x08,0x0E,0x00,
            0x0F,0x01,0x06,0x04,0x0D,0x02,0x00,0x0C,0x0F,0x01,0x08,0x07,0x0A,0x0B,0x00,0x0C,
            0x0F,0x01,0x03,0x0B,0x02,0x08,0x06,0x00,0x0F,0x01,0x04,0x0C,0x07,0x08,0x06,0x00,
            0x0F,0x01,0x0D,0xDE,0xF6,0xF7,0xF8,0xF0,0xFF,0x71,0xF2,0xF4,0xF7,0xF8,0x7C,0x70,
            0xFF,0xF1,0xF2,0xF3,0x7A,0xFB,0xF0,0xF6,0xFF,0x01,0x02,0x03,0x04,0x05,0x00,0x06,
            0x0F,0x01,0x02,0x03,0x0D,0x0E,0x00,0x06,0x0F,0x01,0x0F,0x0E,0x07,0x0A,0x0B,0x00,
            0x0F,0x01,0x02,0x04,0x06,0x07,0x08,0x00,0x0F,0x01,0x03,0x07,0x0A,0x06,0x09,0x00
        };



        static MachineDriver machine_driver = new MachineDriver
        (
                /* basic machine hardware */
                new MachineCPU[]
                {
                    new MachineCPU(
                                CPU_M6809,
                                2000000,	/* 2 Mhz (?) */
                                0,
                                readmem,writemem,null,null,
                                interrupt, 1
                        ),
                        new MachineCPU(
                                CPU_M6809 | CPU_AUDIO_CPU,
                                2000000,	/* 2 Mhz (?) */
                                2,
                                sound_readmem,sound_writemem,null,null,
                                capcom_sh_interrupt,12
                        ),

                },
                60,
                sonson_init_machine,

                /* video hardware */
                32*8, 32*8, new rectangle( 1*8, 31*8-1, 1*8, 31*8-1 ),
                gfxdecodeinfo,
                32,64*4+32*8,
                sonson_vh_convert_color_prom,
                VIDEO_TYPE_RASTER,
                null,
                generic_vh_start,
                generic_vh_stop,
                sonson_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                capcom_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );



        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr sonson_rom= new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);	/* 64k for code + 3*16k for the banked ROMs images */              
                ROM_LOAD( "ss.01e",       0x4000, 0x4000, 0xcd40cc54 );
                ROM_LOAD( "ss.02e",       0x8000, 0x4000, 0xc3476527 );
                ROM_LOAD( "ss.03e",       0xc000, 0x4000, 0x1fd0e729 );

                ROM_REGION(0x10000);	/* temporary space for graphics (disposed after conversion) */                  
                ROM_LOAD( "ss5.v12",      0x00000, 0x2000, 0x990890b1 );	/* characters */
                ROM_LOAD( "ss6.v12",      0x02000, 0x2000, 0x9388ff82 );
                ROM_LOAD( "ss7.v12",      0x04000, 0x4000, 0x32b14b8e );	/* sprites */
                ROM_LOAD( "ss8.v12",      0x08000, 0x4000, 0x9f59014e );
                ROM_LOAD( "ss9.v12",      0x0c000, 0x4000, 0xe240345a );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "ss3.v12",      0xe000, 0x2000, 0x1135c48a );
                 ROM_END();
        }};

        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
        {
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];


                /* check if the hi score table has already been initialized */
     /*TOFIX           if (memcmp(RAM,0x0300,new char[] {0x00,0x01,0x00,0x00},4) == 0 &&
                                memcmp(RAM,0x0320,new char[] {0x00,0x01,0x00,0x00},4) == 0 &&
                                memcmp(RAM,0x00d8,new char[] {0x00,0x01,0x00,0x00},4) == 0 &&	/* high score */
 /*TOFIX                               memcmp(RAM,0x0328,new char[] {0x00,0x02,0x00,0x00},4) == 0 &&
                                memcmp(RAM,0x0358,new char[] {0x00,0x02,0x00,0x00},4) == 0)
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x0300,1,8*5+12*5,f);
                                RAM[0x00d8] = RAM[0x0300];
                                RAM[0x00d9] = RAM[0x0301];
                                RAM[0x00da] = RAM[0x0302];
                                RAM[0x00db] = RAM[0x0303];
                                fclose(f);
                        }

                        return 1;
                }
                else */return 0;	/* we can't load the hi scores yet */
        }};



        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
        {
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];
                FILE f;


          /*TOFIX      if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x0300,1,8*5+12*5,f);
                        fclose(f);
                }*/
        }};
        public static GameDriver  sonson_driver =new GameDriver
        (
                "Son Son",
                "sonson",
                "MIRKO BUFFONI\nNICOLA SALMORIA\nGARY WALTON\nSIMON WALLS",
                machine_driver,

                sonson_rom,
                null, null,
                null,

                null/*TBR*/,input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                color_prom, null, null,
               ORIENTATION_DEFAULT,

                hiload, hisave
        );    
}
