# ========================================
# CÓDIGO MIPS GENERADO AUTOMÁTICAMENTE
# Compilador - Proyecto 3
# Autores: Bayron Rodríguez & Gadir Calderón
# ========================================

.data
    # Strings del sistema
    nl:           .asciiz "\n"
    prompt_int:   .asciiz "Ingrese un entero: "
    prompt_float: .asciiz "Ingrese un float: "
    result_msg:   .asciiz "Resultado: "
    true_str:     .asciiz "true"
    false_str:    .asciiz "false"

    # Variables del programa
    suma_var: .word 0
    a_var: .word 0
    b_var: .word 0
    resta_var: .word 0
    t1_var: .word 0
    t2_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/ab_intermediate.txt
main:
    # Prólogo simplificado main
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # DECLARE a INT
    # a = 10
    li $t0, 10
    sw $t0, a_var

    # DECLARE b INT
    # b = 5
    li $t0, 5
    sw $t0, b_var

    # t1 = a + b
    lw $t1, a_var
    lw $t2, b_var
    add $t0, $t1, $t2
    sw $t0, t1_var

    # DECLARE suma INT
    # suma = t1
    lw $t0, t1_var
    sw $t0, suma_var

    # t2 = a - b
    lw $t1, a_var
    lw $t2, b_var
    sub $t0, $t1, $t2
    sw $t0, t2_var

    # DECLARE resta INT
    # resta = t2
    lw $t0, t2_var
    sw $t0, resta_var

    # WRITE suma
    lw $a0, suma_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE resta
    lw $a0, resta_var
    jal print_int
    la $a0, nl
    jal print_string


# Epílogo simplificado main
exit_main:
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

