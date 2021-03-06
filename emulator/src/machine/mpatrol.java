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


package machine;

import static Z80.Z80.*;
import static Z80.Z80H.*;
import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.mame.*;

public class mpatrol
{
	/* this looks like some kind of protection. The game does strange things */
	/* if a read from this address doesn't return the value it expects. */
	public static ReadHandlerPtr mpatrol_protection_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		Z80_Regs regs = new Z80_Regs();


		if (errorlog != null) fprintf(errorlog,"%04x: read protection\n", Z80_GetPC());
		Z80_GetRegs(regs);
		return regs.DE.L;
	} };
}