# ========================================
# CÓDIGO MIPS UNIVERSAL - SIN HARDCODEO
# Funciona con cualquier nombre de función
# Autores: Bayron Rodríguez & Gadir Calderón
# ========================================

.data
    # Strings del sistema
    nl:           .asciiz "\n"
    prompt_int:   .asciiz "Ingrese un entero: "
    prompt_float: .asciiz "Ingrese un float: "
    prompt_string: .asciiz "Ingrese texto: "
    result_msg:   .asciiz "Resultado: "
    true_str:     .asciiz "true"
    false_str:    .asciiz "false"

    # Constantes booleanas del lenguaje
    true_const:   .word 1
    false_const:  .word 0
    luna_const:   .word 1    # luna = true
    sol_const:    .word 0    # sol = false

    # Variables del programa
    espacio_var: .word 0
    A_var: .word 0
    Numero_var: .word 0
    Mensaje_var: .word 0
    numero_var: .word 0
    Simbolo_var: .word 0
    Saludo_var: .word 0
    letra_var: .word 0
    Hola_mundo_var: .word 0
    Suma_var: .word 0
    Caracteres_var: .word 0
    mensaje_var: .word 0

    # Strings literales
    str_3: .asciiz "Array[2]:"
    str_8: .asciiz "Array[1]"
    str_9: .asciiz "Array[2]"
    str_7: .asciiz "Array[0]"
    str_4: .asciiz "1. Probando arrays..."
    str_5: .asciiz "2. Probando strings y chars..."
    str_1: .asciiz "Array[0]:"
    str_2: .asciiz "Array[1]:"
    str_6: .asciiz "3. Probando mixto..."

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/ad_intermediate.txt
testArrays:
    # UNIVERSAL: Prólogo para testArrays
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # WRITE Array[0]
    la $a0, str_7
    jal print_string
    la $a0, nl
    jal print_string

    # WRITE Array[1]
    la $a0, str_8
    jal print_string
    la $a0, nl
    jal print_string

    # WRITE Array[2]
    la $a0, str_9
    jal print_string
    la $a0, nl
    jal print_string


#  UNIVERSAL: Epílogo para testArrays
exit_testArrays:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

testStrings:
    # UNIVERSAL: Prólogo para testStrings
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # DECLARE mensaje STRING
    # mensaje = Hola_mundo!
    li $t0, 0    # ERROR: No se pudo procesar 'Hola_mundo!'
    sw $t0, mensaje_var

    # DECLARE letra CHAR
    # letra = A
    lw $t0, A_var
    sw $t0, letra_var

    # DECLARE numero CHAR
    # numero = 5
    li $t0, 5
    sw $t0, numero_var

    # WRITE Mensaje
    lw $a0, Mensaje_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE mensaje
    lw $a0, mensaje_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE Saludo
    lw $a0, Saludo_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE letra
    lw $a0, letra_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE Numero
    lw $a0, Numero_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE numero
    lw $a0, numero_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE Simbolo
    lw $a0, Simbolo_var
    jal print_int
    la $a0, nl
    jal print_string


#  UNIVERSAL: Epílogo para testStrings
exit_testStrings:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

testMixed:
    # UNIVERSAL: Prólogo para testMixed
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # DECLARE espacio CHAR
    # UNKNOWN: espacio =
    # WRITE === PRUEBA MIXTA ===
    li $a0, 0    # ERROR: No se pudo procesar '=== PRUEBA MIXTA ==='
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE Suma
    lw $a0, Suma_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE Caracteres
    lw $a0, Caracteres_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE espacio
    lw $a0, espacio_var
    jal print_int
    la $a0, nl
    jal print_string


#  UNIVERSAL: Epílogo para testMixed
exit_testMixed:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

main:
    # UNIVERSAL: Prólogo para main
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # WRITE === INICIANDO PRUEBAS ===
    li $a0, 0    # ERROR: No se pudo procesar '=== INICIANDO PRUEBAS ==='
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE 1. Probando arrays...
    la $a0, str_4
    jal print_string
    la $a0, nl
    jal print_string

    # CALL testArrays 0
    jal testArrays

    # WRITE 2. Probando strings y chars...
    la $a0, str_5
    jal print_string
    la $a0, nl
    jal print_string

    # CALL testStrings 0
    jal testStrings

    # WRITE 3. Probando mixto...
    la $a0, str_6
    jal print_string
    la $a0, nl
    jal print_string

    # CALL testMixed 0
    jal testMixed

    # WRITE === PRUEBAS COMPLETADAS ===
    li $a0, 0    # ERROR: No se pudo procesar '=== PRUEBAS COMPLETADAS ==='
    jal print_int
    la $a0, nl
    jal print_string


#  UNIVERSAL: Epílogo para main
exit_main:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    li $v0, 10
    syscall


# Salida del programa
exit_program:
    li $v0, 10
    syscall
# ========================================
# FUNCIONES DEL SISTEMA
# ========================================

print_int:
    li $v0, 1
    syscall
    jr $ra

print_string:
    li $v0, 4
    syscall
    jr $ra

read_int:
    li $v0, 5
    syscall
    jr $ra

read_float:
    li $v0, 6
    syscall
    jr $ra

print_float_decimal:
    # $a0 contiene el flotante multiplicado por 100
    addi $sp, $sp, -4
    sw $a0, 0($sp)
    bgez $a0, positive_float
    li $v0, 11
    li $a0, 45    # ASCII de '-'
    syscall
    lw $a0, 0($sp)
    sub $a0, $zero, $a0
positive_float:
    li $t1, 100
    div $a0, $t1
    mflo $t2
    mfhi $t3
    move $a0, $t2
    li $v0, 1
    syscall
    li $v0, 11
    li $a0, 46    # ASCII de '.'
    syscall
    bge $t3, 10, print_decimal
    li $v0, 11
    li $a0, 48    # ASCII de '0'
    syscall
print_decimal:
    move $a0, $t3
    li $v0, 1
    syscall
    addi $sp, $sp, 4
    jr $ra

