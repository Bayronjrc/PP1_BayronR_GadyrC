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
    dif_var: .word 0
    otra_var: .word 0
    result_var: .word 0
    A_var: .word 0
    x22_var: .word 0
    t1_var: .word 0
    t2_var: .word 0

.text
.globl main

main:
    # Función main generada automáticamente
    jal mi
    li $v0, 10
    syscall

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/ac_intermediate.txt
mi:
    # 🚀 UNIVERSAL: Prólogo para mi
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # 🚀 UNIVERSAL: Guardar parámetros de mi
    sw $a0, -4($fp)   # dif local
    sw $a0, dif_var     # dif global
    sw $a1, -8($fp)   # otra local
    sw $a1, otra_var     # otra global

    # Inicio de función
    # DECLARE dif INT
    # DECLARE otra CHAR
    # t1 = dif + 1
    lw $t1, -4($fp)   # dif local
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, t1_var

    # RETURN t1
    lw $v0, t1_var
    # Valor de retorno en $v0
    j exit_mi


# 🚀 UNIVERSAL: Epílogo para mi
exit_mi:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

testWeirdNames:
    # 🚀 UNIVERSAL: Prólogo para testWeirdNames
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # DECLARE x22 INT
    # x22 = 5
    li $t0, 5
    sw $t0, x22_var

    # PARAM x22
    lw $a0, x22_var
    # ✅ UNIVERSAL: Parámetro x22 cargado en $a0

    # PARAM A
    lw $a1, A_var
    # ✅ UNIVERSAL: Parámetro A cargado en $a1

    # t2 = CALL mi 2
    jal mi
    sw $v0, t2_var

    # DECLARE result FLOAT
    # result = t2
    lw $t0, t2_var
    sw $t0, result_var

    # WRITE result
    lw $a0, result_var
    jal print_float_decimal
    la $a0, nl
    jal print_string


# 🚀 UNIVERSAL: Epílogo para testWeirdNames
exit_testWeirdNames:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra


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

