/***************************************************************************

  GUNSMOKE
  ========

  Driver provided by Paul Leaman (paull@vortexcomputing.demon.co.uk)

  Please do not send anything large to this address without asking me
  first.

***************************************************************************/

/*
 * ported to v0.29
 * using automatic conversion tool v0.04
 * converted at : 30-05-2012 23:04:54
 *
 *
 * roms are from v0.36 romset
 *
 */ 
package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.capcom.*;
import static sndhrdw.generic.*;
import static machine.gunsmoke.*;
import static vidhrdw.generic.*;
import static vidhrdw.gunsmoke.*;
import static mame.inptport.*;
import static mame.memoryH.*;

public class gunsmoke
{
	
	
	static MemoryReadAddress readmem[] =
	{
		 new MemoryReadAddress( 0x0000, 0xbfff, MRA_ROM ),
	//	new MemoryReadAddress( 0x8000, 0xbfff, gunsmoke_bankedrom_r ),
		new MemoryReadAddress( 0xc000, 0xc000, input_port_0_r ),
		new MemoryReadAddress( 0xc001, 0xc001, input_port_1_r ),
		new MemoryReadAddress( 0xc002, 0xc002, input_port_2_r ),
		new MemoryReadAddress( 0xc003, 0xc003, input_port_3_r ),
		new MemoryReadAddress( 0xc004, 0xc004, input_port_4_r ),
		new MemoryReadAddress( 0xe000, 0xffff, MRA_RAM ), /* Work + sprite RAM */
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new MemoryWriteAddress( 0xc800, 0xc800, soundlatch_w ),
		new MemoryWriteAddress( 0xc804, 0xc804, gunsmoke_bankswitch_w ),  /* Bank switch */
		new MemoryWriteAddress( 0xc806, 0xc806, MWA_NOP ), /* Watchdog ?? */
		new MemoryWriteAddress( 0xd000, 0xd3ff, videoram_w, videoram, videoram_size ),
		new MemoryWriteAddress( 0xd400, 0xd7ff, colorram_w, colorram ),
		new MemoryWriteAddress( 0xd800, 0xd801, MWA_RAM, gunsmoke_bg_scrolly ),
		new MemoryWriteAddress( 0xd802, 0xd802, MWA_RAM, gunsmoke_bg_scrollx ),
		new MemoryWriteAddress( 0xe000, 0xefff, MWA_RAM ),
		new MemoryWriteAddress( 0xf000, 0xffff, MWA_RAM, spriteram, spriteram_size ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	
	
	static MemoryReadAddress sound_readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new MemoryReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
		new MemoryReadAddress( 0xc800, 0xc800, soundlatch_r ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress sound_writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new MemoryWriteAddress( 0xc000, 0xdfff, MWA_RAM ),
		new MemoryWriteAddress( 0xe000, 0xe000, AY8910_control_port_0_w ),
		new MemoryWriteAddress( 0xe001, 0xe001, AY8910_write_port_0_w ),
		new MemoryWriteAddress( 0xe002, 0xe002, AY8910_control_port_1_w ),
		new MemoryWriteAddress( 0xe003, 0xe003, AY8910_write_port_1_w ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	
	
	static InputPortPtr input_ports = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
	
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x03, 0x03, "Bonus", IP_KEY_NONE );
		PORT_DIPSETTING(    0x03, "30k, 100k & every 100k");
		PORT_DIPSETTING(    0x02, "30k, 80k & every 80k" );
		PORT_DIPSETTING(    0x01, "30k & 100K only");
		PORT_DIPSETTING(    0x00, "30k, 100k & every 150k" );
		PORT_DIPNAME( 0x04, 0x04, "Demonstration", IP_KEY_NONE );
		PORT_DIPSETTING(    0x00, "Off");
		PORT_DIPSETTING(    0x04, "On" );
		PORT_DIPNAME( 0x08, 0x00, "Cabinet", IP_KEY_NONE );
		PORT_DIPSETTING(    0x00, "Upright");
		PORT_DIPSETTING(    0x08, "Cocktail");
		PORT_DIPNAME( 0x30, 0x30, "Difficulty", IP_KEY_NONE );
		PORT_DIPSETTING(    0x10, "Easy" );
		PORT_DIPSETTING(    0x30, "Normal" );
		PORT_DIPSETTING(    0x20, "Difficult" );
		PORT_DIPSETTING(    0x00, "Very Difficult" );
		PORT_DIPNAME( 0x40, 0x40, "Freeze", IP_KEY_NONE );
		PORT_DIPSETTING(    0x40, "Off");
		PORT_DIPSETTING(    0x00, "On" );
		PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
		PORT_DIPSETTING(    0x00, "On" );
		PORT_DIPSETTING(    0x80, "Off" );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x07, 0x07, "Coin 2", IP_KEY_NONE );
		PORT_DIPSETTING(    0x00, "4 Coins/1 Credit");
		PORT_DIPSETTING(    0x01, "3 Coins/1 Credit" );
		PORT_DIPSETTING(    0x02, "2 Coins/1 Credit" );
		PORT_DIPSETTING(    0x07, "1 Coin/1 Credit" );
		PORT_DIPSETTING(    0x05, "1 Coin/3 Credits" );
		PORT_DIPSETTING(    0x06, "1 Coin/2 Credits" );
		PORT_DIPSETTING(    0x04, "1 Coin/4 Credits" );
		PORT_DIPSETTING(    0x03, "1 Coin/6 Credits" );
		PORT_DIPNAME( 0x38, 0x38, "Coin 1", IP_KEY_NONE );
		PORT_DIPSETTING(    0x00, "4 Coins/1 Credit");
		PORT_DIPSETTING(    0x08, "3 Coins/1 Credit" );
		PORT_DIPSETTING(    0x10, "2 Coins/1 Credit" );
		PORT_DIPSETTING(    0x38, "1 Coin/1 Credit" );
		PORT_DIPSETTING(    0x30, "1 Coin/2 Credits" );
		PORT_DIPSETTING(    0x28, "1 Coin/3 Credits" );
		PORT_DIPSETTING(    0x20, "1 Coin/4 Credits" );
		PORT_DIPSETTING(    0x18, "1 Coin/6 Credits" );
		PORT_DIPNAME( 0x40, 0x40, "Allow Continue", IP_KEY_NONE );
		PORT_DIPSETTING(    0x00, "No");
		PORT_DIPSETTING(    0x40, "Yes");
		PORT_DIPNAME( 0x80, 0x80, "Demo Sounds", IP_KEY_NONE );
		PORT_DIPSETTING(    0x00, "Off" );
		PORT_DIPSETTING(    0x80, "On");
	INPUT_PORTS_END(); }}; 
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		1024,	/* 1024 characters */
		2,	/* 2 bits per pixel */
		new int[] { 4, 0 },
		new int[] { 7*16, 6*16, 5*16, 4*16, 3*16, 2*16, 1*16, 0*16 },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3},
		16*8	/* every char takes 16 consecutive bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		2048,	/* 2048 sprites */
		4,      /* 4 bits per pixel */
		new int[] { 2048*64*8+4, 2048*64*8+0, 4, 0 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		new int[] { 33*8+3, 33*8+2, 33*8+1, 33*8+0, 32*8+3, 32*8+2, 32*8+1, 32*8+0,
				8+3, 8+2, 8+1, 8+0, 3, 2, 1, 0 },
		64*8	/* every sprite takes 64 consecutive bytes */
	);
	
	static GfxLayout tilelayout = new GfxLayout
	(
		32,32,  /* 32*32 tiles */
		512,    /* 512 tiles */
		4,      /* 2 bits per pixel */
		new int[] { 512*256*8+4, 512*256*8+0, 4, 0 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16,
				16*16, 17*16, 18*16, 19*16, 20*16, 21*16, 22*16, 23*16,
				24*16, 25*16, 26*16, 27*16, 28*16, 29*16, 30*16, 31*16 },
		new int[] { 192*8+8+3, 192*8+8+2, 192*8+8+1, 192*8+8+0, 192*8+3, 192*8+2, 192*8+1, 192*8+0,
				128*8+8+3, 128*8+8+2, 128*8+8+1, 128*8+8+0, 128*8+3, 128*8+2, 128*8+1, 128*8+0,
				64*8+8+3, 64*8+8+2, 64*8+8+1, 64*8+8+0, 64*8+3, 64*8+2, 64*8+1, 64*8+0,
				8+3, 8+2, 8+1, 8+0, 3, 2, 1, 0 },
		256*8	/* every tile takes 256 consecutive bytes */
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x00000, charlayout,              0, 64 ),
		new GfxDecodeInfo( 1, 0x04000, tilelayout,           64*4, 64 ), /* Tiles */
		new GfxDecodeInfo( 1, 0x44000, spritelayout, 2*64*4+16*16, 16 ), /* Sprites */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	/* Incorrect colour PROMS from exedexes. */
	static char color_prom[] =
	{
	        /* red component */
		0x00,0x05,0x06,0x07,0x08,0x04,0x06,0x07,0x05,0x06,0x07,0x08,0x02,0x03,0x05,0x07,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x04,0x05,0x06,0x08,0x03,0x04,0x05,0x07,0x03,0x04,0x05,0x06,0x07,0x08,0x00,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x01,0x05,0x07,0x09,0x0b,0x0d,0x04,0x06,0x08,0x0a,0x0c,0x06,0x09,0x0c,0x0e,0x00,
		0x06,0x08,0x0a,0x0c,0x0e,0x00,0x02,0x04,0x06,0x08,0x05,0x07,0x09,0x0b,0x0d,0x00,
		0x07,0x09,0x0b,0x0d,0x0f,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x00,
		0x0e,0x0e,0x04,0x05,0x09,0x0b,0x0c,0x0c,0x09,0x0e,0x0d,0x09,0x06,0x04,0x07,0x00,
		0x00,0x07,0x00,0x0f,0x09,0x06,0x09,0x0c,0x04,0x00,0x0f,0x0f,0x09,0x0f,0x0f,0x00,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
	        /* green component */
		0x00,0x00,0x00,0x00,0x00,0x04,0x06,0x07,0x03,0x04,0x05,0x06,0x04,0x05,0x07,0x08,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x02,0x03,0x04,0x06,0x05,0x06,0x07,0x09,0x03,0x04,0x05,0x06,0x07,0x08,0x00,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x01,0x05,0x07,0x09,0x0b,0x0d,0x06,0x08,0x0a,0x0c,0x0e,0x00,0x00,0x00,0x00,0x00,
		0x03,0x05,0x07,0x09,0x0b,0x06,0x08,0x0a,0x0c,0x0e,0x05,0x07,0x09,0x0b,0x0d,0x00,
		0x04,0x06,0x08,0x0a,0x0c,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x00,
		0x06,0x0a,0x06,0x09,0x0a,0x0a,0x08,0x06,0x05,0x05,0x03,0x00,0x02,0x04,0x07,0x00,
		0x00,0x00,0x0b,0x0f,0x09,0x05,0x08,0x0b,0x04,0x07,0x0a,0x00,0x0f,0x0f,0x00,0x00,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
	        /* blue component */
		0x00,0x00,0x00,0x00,0x00,0x02,0x04,0x05,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x04,0x05,0x06,0x07,0x08,0x09,0x00,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x01,0x06,0x08,0x0a,0x0c,0x0e,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x03,0x05,0x07,0x09,0x0b,0x00,0x02,0x04,0x06,0x08,0x05,0x07,0x09,0x0b,0x0d,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x00,
		0x00,0x00,0x00,0x00,0x05,0x00,0x00,0x00,0x03,0x00,0x00,0x00,0x00,0x04,0x06,0x00,
		0x01,0x0c,0x0c,0x0f,0x09,0x00,0x00,0x00,0x04,0x0f,0x00,0x07,0x00,0x00,0x00,0x00,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
	        0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
	        /* char lookup table */
		0x0f,0x03,0x0e,0x09,0x0f,0x01,0x01,0x01,0x0f,0x02,0x02,0x02,0x0f,0x03,0x03,0x03,
		0x0f,0x04,0x04,0x04,0x0f,0x05,0x05,0x05,0x0f,0x06,0x06,0x06,0x0f,0x07,0x07,0x07,
		0x0f,0x08,0x08,0x08,0x0f,0x09,0x09,0x09,0x0f,0x0a,0x0a,0x0a,0x0f,0x0b,0x0b,0x0b,
		0x0f,0x0c,0x0c,0x0c,0x0f,0x0d,0x0d,0x0d,0x0f,0x0e,0x0e,0x0e,0x0f,0x00,0x00,0x00,
		0x0f,0x01,0x02,0x03,0x0f,0x08,0x02,0x03,0x08,0x01,0x02,0x03,0x0f,0x0d,0x0e,0x0f,
		0x0f,0x01,0x02,0x03,0x0f,0x05,0x06,0x07,0x0f,0x09,0x0a,0x0b,0x0f,0x0d,0x0e,0x0f,
		0x00,0x03,0x03,0x03,0x00,0x00,0x00,0x00,0x0f,0x02,0x0e,0x00,0x0f,0x0c,0x0e,0x00,
		0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
		0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
	        /* 32x32 tile lookup table */
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x08,0x0f,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        /* Not needed */
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x00,
	        /* sprite lookup table */
		0x0f,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,
		0x0f,0x06,0x07,0x08,0x09,0x0a,0x01,0x02,0x03,0x04,0x05,0x0b,0x0c,0x0d,0x0e,0x00,
		0x0f,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0b,0x0c,0x0d,0x0e,0x00,
		0x0f,0x06,0x06,0x07,0x07,0x08,0x08,0x09,0x09,0x0a,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,
		0x0f,0x00,0x00,0x01,0x01,0x02,0x02,0x03,0x03,0x04,0x04,0x0b,0x0c,0x0d,0x0e,0x00,
		0x0f,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,0x01,0x02,0x03,0x04,0x0b,0x0c,0x0d,0x0e,0x00,
		0x0f,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0b,0x0c,0x0d,0x0e,0x00,
		0x0f,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,0x01,0x02,0x03,0x04,0x0b,0x0c,0x0d,0x0e,0x00,
		0x0f,0x05,0x06,0x07,0x08,0x09,0x00,0x01,0x02,0x03,0x04,0x0b,0x0c,0x0d,0x0e,0x00,
		0x0f,0x00,0x01,0x02,0x03,0x04,0x0a,0x0b,0x0c,0x0d,0x0e,0x0b,0x0c,0x0d,0x0e,0x00,
		0x0f,0x06,0x08,0x0a,0x0c,0x0e,0x00,0x00,0x00,0x01,0x00,0x0b,0x0c,0x0d,0x0e,0x00,
		0x0f,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x00,0x00,0x00,0x0b,0x00,
		0x0f,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x00,0x00,0x0b,0x0c,0x00,
		0x0f,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x00,0x0b,0x0c,0x0d,0x00,
		0x0f,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0b,0x0c,0x0d,0x0e,0x00,
		0x0f,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0e,0x00,
	        /* sprite palette bank */
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
		0x00,0x01,0x01,0x01,0x01,0x01,0x02,0x02,0x02,0x02,0x02,0x00,0x00,0x00,0x00,0x00,
		0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
		0x00,0x01,0x01,0x01,0x01,0x01,0x02,0x02,0x02,0x02,0x02,0x00,0x00,0x00,0x00,0x00,
		0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
		0x00,0x02,0x02,0x02,0x02,0x02,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
		0x00,0x02,0x02,0x02,0x02,0x02,0x00,0x00,0x03,0x03,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
		0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
		0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
		0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
		0x00,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x02,0x00
	};
	
	static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				4000000,        /* 4 Mhz (?) */
				0,
				readmem,writemem,null,null,
				interrupt,1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				3000000,	/* 3 Mhz ??? */
				2,	/* memory region #2 */
				sound_readmem,sound_writemem,null,null,
				capcom_sh_interrupt,12
			)
		},
		60,
		10,	/* 10 CPU slices per frame - enough for the sound CPU to read all commands */
		null,
	
		/* video hardware */
		32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
		gfxdecodeinfo,
		256,64*4+64*4+16*16+16*16,
		gunsmoke_vh_convert_color_prom,
	
		VIDEO_TYPE_RASTER,
		null,
		gunsmoke_vh_start,
		gunsmoke_vh_stop,
		gunsmoke_vh_screenrefresh,
	
		/* sound hardware */
		null,
		null,
		capcom_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);
	
	
	
	static RomLoadPtr gunsmoke_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x20000);    /* 2*64k for code */
		ROM_LOAD(  "09n_gs03.bin", 0x00000, 0x8000, 0x59cd20dd );/* Code 0000-7fff */    /*0.29 filename is "9n_gs03.bin" */
		ROM_LOAD( "10n_gs04.bin", 0x10000, 0x8000, 0x36fec338 );/* Paged code */
		ROM_LOAD( "12n_gs05.bin", 0x18000, 0x8000, 0xfb8bcb4b );/* Paged code */
	
		ROM_REGION(0x84000);    /* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "11f_gs01.bin", 0x00000, 0x4000, 0xf5980000 );/* Characters */
		ROM_LOAD( "06a_gs09.bin",  0x04000, 0x8000, 0xdf196979 );/* 32x32 tiles planes 0-1 */   /*0.29 filename is "6a_gs09.bin" */
		ROM_LOAD( "05a_gs08.bin",  0x0c000, 0x8000, 0x6a5f513f );                               /*0.29 filename is without starting 0 */
		ROM_LOAD( "04a_gs07.bin",  0x14000, 0x8000, 0x80eb6933 );                               /*0.29 filename is without starting 0 */
		ROM_LOAD( "02a_gs06.bin",  0x1c000, 0x8000, 0x6c8ef650 );                               /*0.29 filename is without starting 0 */
		ROM_LOAD( "06c_gs13.bin",  0x24000, 0x8000, 0xb9a10141 );/* 32x32 tiles planes 2-3 */   /*0.29 filename is without starting 0 */
		ROM_LOAD( "05c_gs12.bin",  0x2c000, 0x8000, 0x6ba1f1cf );                               /*0.29 filename is without starting 0 */
		ROM_LOAD( "04c_gs11.bin",  0x34000, 0x8000, 0x3083c58f );                               /*0.29 filename is without starting 0 */
		ROM_LOAD( "02c_gs10.bin",  0x3c000, 0x8000, 0x65aa0244 );                               /*0.29 filename is without starting 0 */
		ROM_LOAD( "06l_gs18.bin",  0x44000, 0x8000, 0x9009aa59 );/* Sprites planes 0-1 */       /*0.29 filename is without starting 0 */    
		ROM_LOAD( "04l_gs17.bin",  0x4c000, 0x8000, 0x8d5bb65f );/* Sprites planes 0-1 */       /*0.29 filename is without starting 0 */ 
		ROM_LOAD( "03l_gs16.bin",  0x54000, 0x8000, 0xbb944980 );/* Sprites planes 0-1 */       /*0.29 filename is without starting 0 */
		ROM_LOAD( "01l_gs15.bin",  0x5c000, 0x8000, 0x58c970b5 );/* Sprites planes 0-1 */       /*0.29 filename is without starting 0 */
		ROM_LOAD( "06n_gs22.bin",  0x64000, 0x8000, 0x6df46970 );/* Sprites planes 2-3 */       /*0.29 filename is without starting 0 */
		ROM_LOAD( "04n_gs21.bin",  0x6c000, 0x8000, 0x237981a3 );/* Sprites planes 2-3 */       /*0.29 filename is without starting 0 */
		ROM_LOAD( "03n_gs20.bin",  0x74000, 0x8000, 0x3d2be527 );/* Sprites planes 2-3 */       /*0.29 filename is without starting 0 */
		ROM_LOAD( "01n_gs19.bin",  0x7c000, 0x8000, 0x4b78e0a2 );/* Sprites planes 2-3 */       /*0.29 filename is without starting 0 */
	
		ROM_REGION(0x10000);/* 64k for the audio CPU */
		ROM_LOAD( "14h_gs02.bin", 0x00000, 0x8000, 0xe9b79a81 );
	
		ROM_REGION(0x8000);
		ROM_LOAD( "11c_gs14.bin", 0x00000, 0x8000, 0xda12ae90 );/* Background tile map */
	ROM_END(); }}; 
	
	public static GameDriver gunsmoke_driver = new GameDriver
	(
		"Gunsmoke",
		"gunsmoke",
		"Paul Leaman (MAME driver)\nRichard Davies\nAnders Nilsson\nMirko Buffoni\nNicola Salmoria\n",
		machine_driver,
	
		gunsmoke_rom,
		null, null,
		null,
	
		null/*TBR*/,input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,
	
		color_prom, null, null,
		ORIENTATION_DEFAULT,
	
		null, null
	);
}
