/* This is a simplefied ELF reader.
 * You can contact me if you find any bugs.
 *
 * Luming Wang<wlm199558@126.com>
 */

#include "kerelf.h"
#include <stdio.h>

#define EI_DATA 5 /* File identification byte 0 index */
#define ENDIANB 2 /* Magic number byte 0 */
#define ENDIANL 1 /* Magic number byte 0 */

/* Overview:
 *   Check whether it is a ELF file.
 *
 * Pre-Condition:
 *   binary must longer than 4 byte.
 *
 * Post-Condition:
 *   Return 0 if `binary` isn't an elf. Otherwise
 * return 1.
 */
int is_elf_format(u_char *binary)
{
    Elf32_Ehdr *ehdr = (Elf32_Ehdr *)binary;
    if (ehdr->e_ident[EI_MAG0] == ELFMAG0 &&
        ehdr->e_ident[EI_MAG1] == ELFMAG1 &&
        ehdr->e_ident[EI_MAG2] == ELFMAG2 &&
        ehdr->e_ident[EI_MAG3] == ELFMAG3)
    {
        return 1;
    }

    return 0;
}

int get_endian_type(u_char *binary)
{
    Elf32_Ehdr *ehdr = (Elf32_Ehdr *)binary;
    if (ehdr->e_ident[EI_DATA] == ENDIANB)
    {
        return 2;
    }
    else if (ehdr->e_ident[EI_DATA] == ENDIANL)
    {
        return 1;
    }
    return 0;
}

u_int32_t reverse_data_32(u_int32_t data)
{
    Elf32_Word s3 = data & 0x000000ff << 24;
    Elf32_Word s2 = data & 0x0000ff00 << 8;
    Elf32_Word s1 = data & 0x00ff0000 >> 8;
    Elf32_Word s0 = data & 0xff000000 >> 24;
    Elf32_Word ans = s3 | s2 | s1 | s0;
    return ans;
}

u_int16_t reverse_data_16(u_int16_t data)
{
    Elf32_Half s3 = data & 0x000f << 12;
    Elf32_Half s2 = data & 0x00f0 << 4;
    Elf32_Half s1 = data & 0x0f00 >> 4;
    Elf32_Half s0 = data & 0xf000 >> 12;
    Elf32_Half ans = s3 | s2 | s1 | s0;
    return ans;
}


/* Overview:
 *   read an elf format binary file. get ELF's information
 *
 * Pre-Condition:
 *   `binary` can't be NULL and `size` is the size of binary.
 *
 * Post-Condition:
 *   Return 0 if success. Otherwise return < 0.
 *   If success, output address of every section in ELF.
 */

int readelf(u_char *binary, int size)
{
    Elf32_Ehdr *ehdr = (Elf32_Ehdr *)binary;

    int Nr;
    int i;
    int endian_type;

    Elf32_Phdr *phdr = NULL;

    Elf32_Half ph_entry_count;
    Elf32_Half ph_entry_size;

    // check whether `binary` is a ELF file.
    if (size < 4 || !is_elf_format(binary))
    {
        printf("not a standard elf format\n");
        return 0;
    }

    // get section table addr, section header number and section header size.
    endian_type = get_endian_type(binary);
    if (endian_type == ENDIANL)
    {
        phdr = (Elf32_Phdr *)(ehdr->e_phoff + (u_char *)binary);
        ph_entry_count = ehdr->e_phnum;
        for (i = 0; i < ph_entry_count; i++)
        {
            printf("%d:0x%x,0x%x\n", i, phdr[i].p_filesz, phdr[i].p_memsz);
        }
    }else{
        phdr = (Elf32_Phdr *)(reverse_data_32(ehdr->e_phoff) + (u_char *)binary);
        ph_entry_count = reverse_data_16(ehdr->e_phnum);
        for (i = 0; i < ph_entry_count; i++)
        {
            printf("%d:0x%x,0x%x\n", i, reverse_data_32(phdr[i].p_filesz), reverse_data_32(phdr[i].p_memsz));
        }
    }
    return 0;
}