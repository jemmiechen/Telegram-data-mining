/*
 * Telegram Data Mining for ATO Gateway Computer
 * LOG to LGD converter
 *
 * Ubuntu:
 *   gcc -O2 -Wall -Wextra -o dvr dvr.c
 *   ./dvr
 *   ./dvr 55
 *
 * Input example:
 *   55
 *
 * Output example:
 *   1155.lgd
 */
#include <errno.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static int is_digit_string(const char *value) {
    if (!value || !value[0]) {
        return 0;
    }
    while (*value) {
        if (*value < '0' || *value > '9') {
            return 0;
        }
        value++;
    }
    return 1;
}

static int file_exists(const char *path) {
    FILE *file = fopen(path, "rb");
    if (!file) {
        return 0;
    }
    fclose(file);
    return 1;
}

static int resolve_input_name(const char *user_input, char *input, size_t input_size) {
    if (is_digit_string(user_input) && strlen(user_input) <= 2) {
        char lower[1024];
        char upper[1024];

        if (snprintf(lower, sizeof(lower), "S11%02d.log", atoi(user_input)) >= (int) sizeof(lower)) {
            return -1;
        }
        if (snprintf(upper, sizeof(upper), "S11%02d.LOG", atoi(user_input)) >= (int) sizeof(upper)) {
            return -1;
        }

        if (file_exists(lower)) {
            snprintf(input, input_size, "%s", lower);
            return 0;
        }
        if (file_exists(upper)) {
            snprintf(input, input_size, "%s", upper);
            return 0;
        }

        snprintf(input, input_size, "%s", lower);
        return 0;
    }

    snprintf(input, input_size, "%s", user_input);
    return 0;
}

static const uint8_t LGD_HEADER[] = {
    0x03, 0x0E, 0x00, 0xFF, 0x01, 0x7F, 0xA0, 0x7A,
    0x43, 0x57, 0xA9, 0x6D, 0x41, 0xB8, 0xFD, 0x6A,
    0xBF, 0x1B, 0x00, 0xFF, 0x02, 0x16, 0x6E, 0x6D,
    0x41, 0x16, 0x6E, 0x6D, 0x41, 0x01, 0x33, 0x33,
    0x6F, 0x41, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00
};

static int make_output_name(const char *input, char *output, size_t output_size) {
    const char *base = strrchr(input, '/');
    const char *name = base ? base + 1 : input;
    const char *start = (name[0] == 'S' || name[0] == 's') ? name + 1 : name;
    size_t len = strlen(start);

    if (len > 4 && strcmp(start + len - 4, ".log") == 0) {
        len -= 4;
    } else if (len > 4 && strcmp(start + len - 4, ".LOG") == 0) {
        len -= 4;
    }

    if (len + 5 > output_size) {
        return -1;
    }

    memcpy(output, start, len);
    memcpy(output + len, ".lgd", 5);
    return 0;
}

static long file_length(FILE *file) {
    long current = ftell(file);
    long length;

    if (current < 0) {
        return -1;
    }
    if (fseek(file, 0L, SEEK_END) != 0) {
        return -1;
    }
    length = ftell(file);
    if (fseek(file, current, SEEK_SET) != 0) {
        return -1;
    }
    return length;
}

static int convert_log_to_lgd(const char *input_path, const char *output_path) {
    FILE *input = fopen(input_path, "rb");
    FILE *output;
    long input_len;
    long record_count;
    long index;

    if (!input) {
        fprintf(stderr, "Cannot open input '%s': %s\n", input_path, strerror(errno));
        return 1;
    }

    input_len = file_length(input);
    if (input_len < 0) {
        fprintf(stderr, "Cannot read input length '%s'\n", input_path);
        fclose(input);
        return 1;
    }

    output = fopen(output_path, "wb");
    if (!output) {
        fprintf(stderr, "Cannot open output '%s': %s\n", output_path, strerror(errno));
        fclose(input);
        return 1;
    }

    if (fwrite(LGD_HEADER, 1, sizeof(LGD_HEADER), output) != sizeof(LGD_HEADER)) {
        fprintf(stderr, "Cannot write LGD header\n");
        fclose(input);
        fclose(output);
        return 1;
    }

    record_count = input_len / 45;
    for (index = 0; index < record_count; index++) {
        uint8_t record[45];

        if (fread(record, 1, sizeof(record), input) != sizeof(record)) {
            fprintf(stderr, "Input ended unexpectedly at record %ld\n", index + 1);
            fclose(input);
            fclose(output);
            return 1;
        }

        if (fputc(record[0], output) == EOF || fputc(0x00, output) == EOF) {
            fprintf(stderr, "Cannot write output record %ld\n", index + 1);
            fclose(input);
            fclose(output);
            return 1;
        }

        if (fwrite(record + 1, 1, 41, output) != 41) {
            fprintf(stderr, "Cannot write output record %ld\n", index + 1);
            fclose(input);
            fclose(output);
            return 1;
        }
    }

    fclose(input);
    if (fclose(output) != 0) {
        fprintf(stderr, "Cannot close output '%s': %s\n", output_path, strerror(errno));
        return 1;
    }

    if (input_len % 45 != 0) {
        fprintf(stderr, "Warning: ignored %ld trailing byte(s) from '%s'\n", input_len % 45, input_path);
    }

    printf("Converted %s -> %s (%ld records)\n", input_path, output_path, record_count);
    return 0;
}

int main(int argc, char **argv) {
    char user_input[1024];
    char input[1024];
    char output[1024];

    puts("Welcome to Telegram Data Mining for ATO Gateway Computer. (LOG to LGD)");
    puts("Written by Jimmy Chen. Ubuntu/APK conversion build.\n");

    if (argc >= 2) {
        snprintf(user_input, sizeof(user_input), "%s", argv[1]);
    } else {
        printf("Enter last two digits of file name, for example 55 for S1155.log/S1155.LOG:\n");
        if (scanf("%1023s", user_input) != 1) {
            fprintf(stderr, "No input provided\n");
            return 1;
        }
    }

    if (resolve_input_name(user_input, input, sizeof(input)) != 0) {
        fprintf(stderr, "Cannot resolve input '%s'\n", user_input);
        return 1;
    }

    if (argc >= 3) {
        snprintf(output, sizeof(output), "%s", argv[2]);
    } else if (make_output_name(input, output, sizeof(output)) != 0) {
        fprintf(stderr, "Cannot derive output file name from '%s'\n", input);
        return 1;
    }

    return convert_log_to_lgd(input, output);
}
